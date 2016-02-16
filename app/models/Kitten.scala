package models

import java.util.Date

import controllers.Application
import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Kitten(source: String, image: Array[Byte], date: Date)

object Kitten {
  val mapping = str("source") ~
    byteArray("image") ~
    date("date") map {
    case source ~ image ~ date =>
      Kitten(source, image, date)
  }

//  def findAll(): Seq[Kitten] = DB.withConnection { implicit c =>
//    SQL"select * from kitten".as(Kitten.mapping.*)
//  }

  def findFresh(): Option[Kitten] = {
    clean()
    DB.withConnection { implicit c =>
      val fresh = Application.freshness_minutes
      SQL"select * from kitten where date > DATEADD('MINUTE', -${fresh}, NOW())".as(Kitten.mapping.singleOpt)
    }
  }

  def insert(kitten: Kitten): Unit = {
    DB.withConnection { implicit conn =>
      SQL"insert into kitten(source, image, date) values (${kitten.source}, ${kitten.image}, ${kitten.date})".executeUpdate()
    }
  }

  def clean(): Unit = {
    DB.withConnection { implicit c =>
      SQL"delete from kitten where date < DATEADD('MINUTE', -5, NOW())".execute()
    }
  }
}