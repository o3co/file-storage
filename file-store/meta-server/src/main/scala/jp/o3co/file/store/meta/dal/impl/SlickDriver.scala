package jp.o3co.file.store
package meta 
package dal
package impl 

import slick.driver.JdbcProfile
import slick.jdbc.meta.MTable
import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID
import java.net.URI
import java.sql.Timestamp
import java.util.Date

/**
 * SlickDriver implementation to save metas
 */
trait SlickDriver extends Implicits {

  val profile: JdbcProfile

  implicit def executionContext: ExecutionContext

  import profile.api._

  def database: Database 

  implicit val idColumnType = MappedColumnType.base[ResourceId, UUID](_.uuid, ResourceId(_))
  implicit val resourceNameColumnType = MappedColumnType.base[ResourceName, String](_.name, ResourceName(_))
  implicit val storedPathColumnType = MappedColumnType.base[StoredPath, String](_.uri.toString, {x => StoredPath(new URI(x))})
  implicit val statusColumnType = MappedColumnType.base[Status, String](_.name, Status(_))
  implicit val contentTypeColumnType = MappedColumnType.base[ContentType, String](_.toString, ContentType(_))
  implicit val dateColumnType = MappedColumnType.base[Date, Timestamp](d => new Timestamp(d.getTime), t => new Date(t.getTime))

  //implicit val timestampColumnType = MappedColumnType.base[Timestamp, Timestamp]({x => new Timestamp(x.value)}, {x => Timestamp(x.getTime)})

  class Metas(tag: Tag) extends Table[Meta](tag, "meta") {
    def id           = column[ResourceId]("id", O.PrimaryKey, O.SqlType("UUID"))
    def contentType  = column[ContentType]("content_type", O.SqlType("varchar(100)"))
    def name         = column[Option[ResourceName]]("name", O.SqlType("varchar(255)"))
    def storedPath   = column[Option[StoredPath]]("stored_path", O.SqlType("varchar(255)"))
    def status       = column[Status]("status", O.SqlType("varchar(100)"))
    def created      = column[Date]("created", O.SqlType("timestamp"), O.Default(new Timestamp(System.currentTimeMillis())))
    def updated      = column[Date]("updated", O.SqlType("timestamp"), O.Default(new Timestamp(System.currentTimeMillis())))
    def parentId     = column[Option[ResourceId]]("parent_id", O.SqlType("UUID"))

    def * = (id, contentType, name, storedPath, status, created, updated, parentId) <> (Meta.tupled, Meta.unapply)
    def fkParentMeta = foreignKey("parent_fk", parentId, metas)(_.id.?)

    def children     = TableQuery[Metas].filter(id === _.parentId)
  }

  def metas = TableQuery[Metas]

  def createTables: Future[Unit] = {
    database.run(MTable.getTables).flatMap { tableMetas => 
      val defined = tableMetas.map(m => m.name.name)
      val tableSchemas = Seq(metas).collect {
          case t if(!defined.contains(t.baseTableRow.tableName)) => t.schema
        }

      if(tableSchemas.isEmpty) Future((): Unit)
      else database.run(tableSchemas.reduce(_ ++ _).create)
    }
  }

  def dropTables: Future[Unit] = {
    database.run(MTable.getTables).flatMap { tableMetas => 
      val defined = tableMetas.map(tm => tm.name.name)

      val tableSchemas = Seq(metas).collect {
        case t if(defined.contains(t.baseTableRow.tableName)) => t.schema
      }

      if(tableSchemas.isEmpty) Future((): Unit)
      else database.run(tableSchemas.reduce(_ ++ _).drop)
    }
  }

  /**
   *
   */
  def count: Future[Size] = database.run(metas.length.result).map(s => s: Size)

  /**
   *
   */
  def contains(id: ResourceId): Future[Boolean] = {
    database.run(metas.filter(_.id === id).length.result).map(s => s > 0)
  }

  /**
   * Get ids of the meta
   */
  def indexes(offset: Offset, size: Size): Future[Seq[ResourceId]] = {
    database.run(metas.map(_.id).drop(offset).take(size).sortBy(row => row.asc).result)
  }

  def relatives(id: ResourceId): Future[Map[SegmentName, ResourceId]] = {
    val ret = database.run((for {
      e <- metas.filter(_.id === id)
      r <- e.children.filter(_.storedPath.isDefined)
    } yield(e, r)).map(row => (row._2.storedPath.get, row._2.id)).result)
    
    ret.map { srs => 
      //srs.map((k, v) => (k.segment, v)).toMap
      srs.map(v => (v._1.segment, v._2)).toMap
    }
  }

  /**
   *
   */
  def get(id: ResourceId): Future[Option[Meta]] = {
    database.run(metas.filter(_.id === id).result.headOption)
  }

  /**
   *
   */
  def getMulti(ids: Set[ResourceId]): Future[Map[ResourceId, Meta]] = {
    database.run(metas.filter(_.id inSet ids).result)
      .map { ret => 
        ret.map(r => (r.id, r)).toMap
      }
  }

  /**
   *
   */
  def getSegmentsFor(id: ResourceId): Future[Seq[Meta]] = {
    database.run(metas.filter(_.parentId === id).result)
  }

  def update(update: UpdateMeta): Future[Option[Meta]] = {
    get(update.id)
      .flatMap { opMeta => 
        opMeta
          .map { meta => 
            val updated = meta.updateWith(update)
            put(updated).map { unit => 
              Option(updated)
            }
          }
          .getOrElse(Future(None))
      }
  }

  def update(update: BatchUpdateMeta): Future[Option[Meta]] = {
    get(update.id)
      .flatMap { opMeta => 
        opMeta
          .map { meta => 
            val updated = meta.updateWith(update)
            put(updated).map { unit => 
              Option(updated)
            }
          }
          .getOrElse(Future(None))
      }
  }

  /**
   *
   */
  def put(in: Meta): Future[Unit] = {
    database.run(metas.insertOrUpdate(in)) 
      .map(_ => (): Unit)
  }

  /**
   *
   */
  def putMulti(ins: Set[Meta]): Future[Unit] = {
    database.run(DBIO.sequence(ins.iterator.map(i => metas.insertOrUpdate(i))))
      .map(_ => (): Unit)
  }

  /**
   *
   */
  def delete(id: ResourceId): Future[Option[Meta]] = {
    val query = metas.filter(_.id === id)
    database.run(query.result.headOption)
      .flatMap { fetchedOp => 
        fetchedOp.map { fetched =>
          database.run(query.delete).map(_ => Option(fetched))
        }.getOrElse(Future(None))
      }
  }

  /**
   *
   */
  def deleteMulti(ids: Set[ResourceId]): Future[Map[ResourceId, Meta]] = {
    val query = metas.filter(_.id inSet ids)
    database.run(query.result) 
      .flatMap { fetched => 
        val metaMap = fetched.map(r => (r.id, r)).toMap
        if(fetched.nonEmpty) database.run(query.delete).map(_ => metaMap)
        else Future(metaMap)
      }
  }
}
