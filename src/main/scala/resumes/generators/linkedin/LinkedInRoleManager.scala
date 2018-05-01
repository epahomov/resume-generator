package resumes.generators.linkedin

import java.util.UUID

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates.addToSet
import net.liftweb.json.parse
import org.bson.Document
import resumes.MongoDB
import resumes.company.CompanyManager.Companies
import resumes.generators.linkedin.LinkedInParser.LinkedInEmployment
import resumes.generators.linkedin.LinkedInRoleManager.LinkedInRole

import scala.collection.JavaConverters._

object LinkedInRoleManager {

  case class LinkedInRole(
                           id: String = UUID.randomUUID().toString,
                           personUrl: String,
                           employment: LinkedInEmployment,
                           companiesInWhichBeenUsed: List[String]
                         )
}

class LinkedInRoleManager(database: MongoDatabase) {

  private lazy val roles = {
    MongoDB.createCollectionIfNotExists(LINKEDIN_ROLE_COLLECTION_NAME, database)
    database.getCollection(LINKEDIN_ROLE_COLLECTION_NAME)
  }

  implicit val formats = net.liftweb.json.DefaultFormats

  val LINKEDIN_ROLE_COLLECTION_NAME = "linkedin_role"

  def addRole(role: LinkedInRole) = {
    MongoDB.insertValueIntoCollection(role, roles)
  }

  def markRoleAsUsedForApplication(id: String, company: Companies.Value): Long = {
    val filter = Filters.eq("id", id)
    val update = addToSet("companiesInWhichBeenUsed", company.toString)
    roles.updateOne(filter, update).getModifiedCount
  }

  def peopleUrlsProcessed(): Set[String] = {
    roles.find().asScala.map(fromDoc).map(_.personUrl).toSet
  }

  def getNotUsedRole(company: Companies.Value): List[LinkedInRole] = {
    val filter = Filters.not(Filters.in("companiesInWhichBeenUsed", company.toString))
    roles
      .find(filter)
      .asScala
      .toList
      .map(fromDoc)
  }

  private def fromDoc(document: Document): LinkedInRole = {
    parse(document.toJson()).extract[LinkedInRole]
  }

}
