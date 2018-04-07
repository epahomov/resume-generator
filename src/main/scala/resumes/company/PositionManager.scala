package resumes.company

import java.util.UUID

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates._
import net.liftweb.json.parse
import resumes.MongoDB
import resumes.MongoDB.formats
import resumes.company.CompanyManager.Companies
import resumes.company.CompanyManager.Companies.Degree
import resumes.company.PositionManager.Area.Area
import resumes.company.PositionManager.ExperienceLevel.ExperienceLevel
import resumes.company.PositionManager.Position
import resumes.generators.education.Enums.Major

import scala.collection.JavaConverters._
import scala.util.Random

object PositionManager {


  case class Position(
                       company: String,
                       url: String,
                       id: String = UUID.randomUUID().toString,
                       active: Boolean = true,
                       failedAttemptsToApply: Int = 0,
                       requiredMajor: Option[Major] = None,
                       area: Option[Area] = None,
                       experienceLevel: Option[ExperienceLevel] = None,
                       minimumDegreeNecessary: Option[Degree] = None
                     )

  object Area extends Enumeration {
    type Area = Value
    val Computer_Science = Value("Computer Science")
    val Hardware = Value("Hardware")
    val Design = Value("Design")
    val Finance = Value("Finance")
    val PR = Value("PR")
  }

  object ExperienceLevel extends Enumeration {
    type ExperienceLevel = Value
    val Freshly_Graduate = Value("Freshly graduate")
    val Beginner = Value("Beginner")
    val Middle = Value("Middle")
    val Senior = Value("Senior")
  }

}

class PositionManager(database: MongoDatabase) {

  val POSITIONS_COLLECTION = "positions2"
  val MAX_FAILED_ATTEMPS = 3

  lazy val positions = {
    MongoDB.createCollectionIfNotExists(POSITIONS_COLLECTION, database)
    database.getCollection(POSITIONS_COLLECTION)
  }

  def uploadPositions(pos: List[Position]) = {
    MongoDB.insertIntoCollection(pos, positions)
  }


  def getRandomPosition(company: Companies.Value): Position = {
    val filter = Filters.and(Filters.eq("company", company.toString), Filters.eq("active", true))
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

  def successfullyAppliedForPosition(id: String): Unit = {
    positions.updateOne(Filters.eq("id", id), set("failedAttemptsToApply", 0))
  }

  private def deactivatePosition(id: String) = {
    positions.updateOne(Filters.eq("id", id), set("active", false))
  }

}
