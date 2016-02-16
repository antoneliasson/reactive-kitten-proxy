package controllers

import java.io.{File, FileOutputStream}
import javax.inject.Inject

import play.api.libs.ws.WSClient
import play.api.libs.ws.ning.NingWSResponse
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.xml.Elem

class Application @Inject()(ws: WSClient) extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  // cruft
  private def fresh_cat(file: File): Boolean = file.isFile() && System.currentTimeMillis() - file.lastModified() < Application.freshness_seconds

  // cruft
  def write_cat(response: Future[NingWSResponse], file: File): Future[Array[Byte]] = response.map {
    response: NingWSResponse => {
      val stream = new FileOutputStream(file)
      stream.write(response.bodyAsBytes)
      stream.close()
      response.bodyAsBytes
    }
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
    val metacat = get_metacat()
    metacat.flatMap {
      xml => {
        val image = xml \ "data" \ "images" \ "image"

        val url = (image \ "url").text
        val source = (image \ "source_url").text

        val kitten: Future[Array[Byte]] = download_cat(url)
        val fr: Future[Result] = kitten.map(bytes => Ok(bytes))
        fr
      }
    }
  }
}

object Application {
  val cache_filename = "/tmp/kittenproxy1.png"
  val freshness_seconds = 10 * 60
}