package controllers

import java.util.Date
import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import models.Kitten
import play.api.Play.current
import play.api.db.DB
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.SystemProperties
import scala.xml.Elem

class Application @Inject()(ws: WSClient) extends Controller {
  def index = Action {
    Kitten.clean()
    val kittens = Kitten.findAll()
    Ok(views.html.index("Current number of kittens on scratching post: " + kittens.length, kittens))
  }

  private def get_metacat(): Future[Elem] = {
    val prop = new SystemProperties()
    val api_key = prop.getOrElse("kittenproxy.apikey", "")
    val body: Future[Elem] = ws
      .url(s"http://thecatapi.com/api/images/get?format=xml&type=png&size=med&api_key=$api_key")
      .get()
      .map {
        response => {
          response.xml
        }
      }
    body
  }

  private def download_cat(url: String): Future[Array[Byte]] = ws
    .url(url)
    .get()
    .map {
      resp => resp.bodyAsBytes
    }

  def dynamic_kitten = Action.async {
    Kitten.findFresh() match {
      case Some(k) => Future(Ok(k.image).as("image/png").withHeaders(CONTENT_LENGTH -> k.image.length.toString))
      case None => {
        val metacat = get_metacat()
        metacat.flatMap {
          xml => {
            val image = xml \ "data" \ "images" \ "image"

            val id = (image \ "id").text
            val url = (image \ "url").text
            val source = (image \ "source_url").text

            val kitten: Future[Array[Byte]] = download_cat(url)
            val fr: Future[Result] = kitten.map {
              bytes => {
                Kitten.insert(Kitten(id, source, bytes, new Date()))
                Ok(bytes).as("image/png").withHeaders(CONTENT_LENGTH -> bytes.length.toString)
              }
            }
            fr
          }
        }
      }
    }
  }

  def static_kitten(id: String) = Action {
    Kitten.clean()
    val kitten = Kitten.find(id)
    kitten match {
      case Some(k) => Ok(k.image).as("image/png")
      case None => NotFound
    }
  }
}

object Application {
  val freshness_minutes = 10
  val clean_hours = 24
}