package resumes.company

import java.util.UUID

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates._
import net.liftweb.json.parse
import resumes.MongoDB
import resumes.MongoDB.formats
import resumes.company.CompanyManager.Companies
import resumes.company.PositionManager.Position
import resumes.generators.Utils

import scala.collection.JavaConverters._

object PositionManager {


  case class Position(
                       company: String,
                       url: String,
                       id: String = UUID.randomUUID().toString,
                       active: Boolean = true,
                       failedAttemptsToApply: Int = 0,
                       requiredMajor: Option[String] = None,
                       area: Option[String] = None,
                       experienceLevel: Option[String] = None,
                       previousPosition: Option[String] = None,
                       popularity: Option[Int] = Some(10),
                       minimumDegreeNecessary: Option[String] = None,
                       address: Option[String] = None,
                       skills: Option[List[String]] = None,
                       parsingComments: Option[List[String]] = None,
                       text: Option[String] = None
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

  val POSITIONS_COLLECTION = "positions3"
  val MAX_FAILED_ATTEMPS = 3

  lazy val positions = {
    MongoDB.createCollectionIfNotExists(POSITIONS_COLLECTION, database)
    database.getCollection(POSITIONS_COLLECTION)
  }

  def uploadPositions(pos: List[Position]) = {
    pos.foreach(position => {
      if (getPositionByUrl(position.url).isDefined) {
        positions.replaceOne(Filters.eq("url", position.url), resumes.Utils.toDoc(position))
      } else {
        MongoDB.insertValueIntoCollection(position, positions)
      }
    })
  }


  def getRandomPosition(company: Companies.Value): Position = {
    val filter = Filters.and(Filters.eq("company", company.toString), Filters.eq("active", true))
    val positionsSnapshot = positions.find(filter).asScala.map(doc => {
      val position = parse(doc.toJson).extract[Position]
      (position, position.popularity.getOrElse(10))
    }).toList
    Utils.getGeneratorFrequency(positionsSnapshot).sample()
  }

  def getPositionById(id: String): Option[Position] = {
    positions.find(Filters.eq("id", id)).first() match {
      case null => None
      case doc => Some(parse(doc.toJson()).extract[Position])
    }
  }

  def getAllPositions(): List[Position] = {
    positions.find().asScala.map(doc => {parse(doc.toJson).extract[Position]}).toList
  }

  def getPositionByUrl(url: String): Option[Position] = {
    positions.find(Filters.eq("url", url)).first() match {
      case null => None
      case doc => Some(parse(doc.toJson()).extract[Position])
    }
  }

  def updateAddressByUrl(url: String, address: String): Unit = {
    positions.updateOne(Filters.eq("url", url), set("address", address))
  }

  def updateSkillsByUrl(url: String, skills: List[String]): Unit = {
    val position = getPositionByUrl(url).get.copy(skills = Some(skills))
    positions.replaceOne(Filters.eq("url", url), resumes.Utils.toDoc(position))
  }

  def removeByUrl(url: String): Unit = {
    positions.deleteOne(Filters.eq("url", url))
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
