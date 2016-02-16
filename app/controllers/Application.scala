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
import scala.xml.Elem

class Application @Inject()(ws: WSClient) extends Controller {
  def index = Action {
    val no: Option[Int] = DB.withConnection { implicit c =>
      val res: SqlQueryResult = SQL"Select count(source) from kitten".executeQuery()
      res.as(scalar[Int].singleOpt)
    }
    //val result: Boolean = SQL"Select 1".execute()
    Ok(views.html.index("No. of kittens: " + no.get))
  }

  private def get_metacat(): Future[Elem] = {
    val body: Future[Elem] = ws
      .url("http://thecatapi.com/api/images/get?format=xml&type=png&size=med")
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
      case Some(k) => Future(Ok(k.image).as("image/png"))
      case None => {
        val metacat = get_metacat()
        metacat.flatMap {
          xml => {
            val image = xml \ "data" \ "images" \ "image"

            val url = (image \ "url").text
            val source = (image \ "source_url").text

            val kitten: Future[Array[Byte]] = download_cat(url)
            val fr: Future[Result] = kitten.map {
              bytes => {
                Kitten.insert(Kitten(source, bytes, new Date()))
                Ok(bytes).as("image/png")
              }
            }
            fr
          }
        }
      }
    }
  }
}

object Application {
  val freshness_minutes = 1
}