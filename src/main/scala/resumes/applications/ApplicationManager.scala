package resumes.applications

import java.util.{Date, UUID}

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.{Filters, Updates}
import net.liftweb.json.parse
import org.apache.commons.lang3.RandomStringUtils
import org.apache.logging.log4j.LogManager
import resumes.MongoDB
import resumes.applications.ApplicationManager.Application
import resumes.company.PositionManager
import resumes.emails.EmailsManager
import resumes.response.ResponseManager
import resumes.response.ResponseManager.Response
import resumes.Utils._

import scala.collection.JavaConverters._
import MongoDB.formats
import resumes.generators.person.PersonGenerator
import resumes.generators.person.PersonGenerator.Person

object ApplicationManager {

  case class Application(person: Person,
                         company: String,
                         positionUrl: String,
                         positionId: String,
                         email: String,
                         id: String = UUID.randomUUID().toString,
                         passwordToAccount: String = RandomStringUtils.randomAlphanumeric(8, 12) + "!",
                         response: Option[Response] = None,
                         date: Date = new Date()
                        )
}

class ApplicationManager(emailsManager: EmailsManager,
                         positionsManager: PositionManager,
                         database: MongoDatabase,
                        ) {

  private val APPLICATION_COLLECTION = "applications"

  private val logger = LogManager.getLogger(this.getClass)

  private lazy val applications = {
    MongoDB.createCollectionIfNotExists(APPLICATION_COLLECTION, database)
    database.getCollection(APPLICATION_COLLECTION)
  }

  // Handling response
  def getEmailsManager = emailsManager

  def getAllApplicationsWithUnknownResponse(): List[Application] = {
    val filter = Filters.or(
      Filters.eq("response.decision", ResponseManager.UNKNOWN),
      Filters.not(Filters.exists("response.decision")))
    applications.find(filter).asScala.toList.map(_.toJson).map(parse(_).extract[Application])
  }

  def updateResponse(applicationId: String, response: Response): Unit = {
    val filter = Filters.eq("id", applicationId)
    applications.updateOne(filter, Updates.set("response", toDoc(response)))
  }

  // Supporting submitting of application
  def storeApplication(application: Application) = {
    logger.info(s"Storing application ${application.id}")
    MongoDB.insertValueIntoCollection(application, applications)
  }

  def getApplication(id: String): Application = {
    parse(applications.find(Filters.eq("id", id)).first().toJson()).extract[Application]
  }

  def updateAllComponents(application: Application) = {
    logger.info(s"Updating all components for application ${application.id}")
    emailsManager.markEmailAsUsedForApplication(email = application.email,
      company = application.company)
    positionsManager.successfullyAppliedForPosition(application.positionId)
  }

  def failApplication(application: Application) = {
    logger.info(s"Failing application ${application.id}")
    positionsManager.failedToApplyToPosition(application.positionId)
  }

  def createApplication(company: String): Application = {
    val position = positionsManager.getRandomPosition(company)
    val person = PersonGenerator.generatePerson(position)
    val email = emailsManager.getNotUsedEmail(company).get
    Application(
      person = person,
      company = company,
      positionUrl = position.url,
      positionId = position.id,
      email = email
    )
  }


}
