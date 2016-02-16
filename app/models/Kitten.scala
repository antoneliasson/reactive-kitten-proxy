package models

import java.util.Date

import controllers.Application
import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Kitten(id: String, source: String, image: Array[Byte], date: Date)

object Kitten {
  val mapping = str("id") ~
    str("source") ~
    byteArray("image") ~
    date("date") map {
    case id ~ source ~ image ~ date =>
      Kitten(id, source, image, date)
  }

  def findAll(): Seq[Kitten] = DB.withConnection { implicit c =>
    SQL"select * from kitten order by date desc".as(Kitten.mapping.*)
  }

  def findFresh(): Option[Kitten] = {
    clean()
    DB.withConnection { implicit c =>
      val fresh = Application.freshness_minutes
      SQL"select * from kitten where date > DATEADD('MINUTE', -${fresh}, NOW())".as(Kitten.mapping.singleOpt)
    }
  }

  def find(id: String): Option[Kitten] = {
    DB.withConnection { implicit c =>
      SQL"select * from kitten where id=${id}".as(Kitten.mapping.singleOpt)
    }
  }

  def insert(kitten: Kitten): Unit = {
    DB.withConnection { implicit conn =>
      SQL"insert into kitten(id, source, image, date) values (${kitten.id}, ${kitten.source}, ${kitten.image}, ${kitten.date})".executeUpdate()
    }
  }

  def clean(): Unit = {
    DB.withConnection { implicit c =>
      SQL"delete from kitten where date < DATEADD('MINUTE', -5, NOW())".execute()
    }
  }
}