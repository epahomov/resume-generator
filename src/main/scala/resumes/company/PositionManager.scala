package resumes.company

import java.util.UUID

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import resumes.MongoDB
import resumes.company.PositionManager.Position

import scala.collection.JavaConverters._
import scala.util.Random
import net.liftweb.json.parse
import com.mongodb.client.model.Updates._
import MongoDB.formats

object PositionManager {

  case class Position(
                       company: String,
                       url: String,
                       id: String = UUID.randomUUID().toString,
                       active: Boolean = true,
                       failedAttemptsToApply: Int = 0
                     )

}

class PositionManager(database: MongoDatabase) {

  val POSITIONS_COLLECTION = "positions"
  val MAX_FAILED_ATTEMPS = 3

  lazy val positions = {
    MongoDB.createCollectionIfNotExists(POSITIONS_COLLECTION, database)
    database.getCollection(POSITIONS_COLLECTION)
  }

  def uploadPositions(pos: List[Position]) = {
    MongoDB.insertIntoCollection(pos, positions)
  }


  def getRandomPosition(company: String): Position = {
    val filter = Filters.and(Filters.eq("company", company), Filters.eq("active", true))
    val positionsSnapshot = positions.find(filter).asScala.map(doc => {
      parse(doc.toJson).extract[Position]
    }).toArray
    positionsSnapshot(Random.nextInt(positionsSnapshot.size))
  }

  def getPositionById(id: String): Option[Position] = {
    positions.find(Filters.eq("id", id)).first() match {
      case null => None
      case doc => Some(parse(doc.toJson()).extract[Position])
    }
  }


  def failedToApplyToPosition(id: String) = {
    positions.updateOne(Filters.eq("id", id), inc("failedAttemptsToApply", 1))
    val position = getPositionById(id)
    if (position.get.failedAttemptsToApply > MAX_FAILED_ATTEMPS) {
      deactivatePosition(id)
    }
  }

  def successfullyAppliedForPosition(id: String) = {
    positions.updateOne(Filters.eq("id", id), set("failedAttemptsToApply", 0))
  }

  private def deactivatePosition(id: String) = {
    positions.updateOne(Filters.eq("id", id), set("active", false))
  }

}
