package resumes.applications

import java.util.{Date, UUID}

import com.mongodb.client.MongoDatabase
import org.apache.commons.lang3.RandomStringUtils
import org.apache.logging.log4j.LogManager
import resumes.MongoDB
import resumes.applications.ApplicationManager.Application
import resumes.company.PositionManager
import resumes.emails.EmailsManager
import resumes.generators.PeopleManager
import resumes.generators.PeopleManager.Person
import resumes.generators.education.EducationGenerator.{Degree, Education}
import resumes.generators.education.UniversityGenerator.University
import resumes.generators.name.FirstNameGenerator.{Gender, Origin}
import resumes.generators.name.NameGenerator.Name
import resumes.generators.person.AddressGenerator.Address
import resumes.generators.person.PhoneNumberGenerator

import scala.util.Random

object ApplicationManager {

  case class Application(person: Person,
                         company: String,
                         positionUrl: String,
                         positionId: String,
                         email: String,
                         id: String = UUID.randomUUID().toString,
                         passwordToAccount: String = RandomStringUtils.randomAlphanumeric(8, 12) + "!",
                         date: Date = new Date()
                        )



  def veryPlainApplication(company: String, positionUrl: String) = {
    val associate = Education(startYear = 2012, endYear = 2014, university = University("Stanford", "Palo Alto", "CA"), degree = Degree.Associate.toString)
    val bachelor = Education(startYear = 2014, endYear = 2016, university = University("Stanford", "Palo Alto", "CA"), degree = Degree.Bachelor.toString)
    val masters = Education(startYear = 2016, endYear = 2018, university = University("Stanford", "Palo Alto", "CA"), degree = Degree.Master.toString)
    val education = List(masters, bachelor, associate)
    val address = Address(zipCode = "94402",
      stateFullName = "California",
      stateShortName = "CA",
      city = "Palo Alto",
      street = "Not your business drive",
      house = "1234"
    )

    val person = Person(
      id = "1234",
      name = Name(
        firstName = "TestingFirstName",
        lastName = "TestingLastName"
      ),
      education = education,
      address = address,
      phoneNumber = PhoneNumberGenerator.generateRandomNumber(),
      gender = Gender.Male,
      origin = Origin.US
    )
    Application(person = person,
      company = company,
      positionUrl = positionUrl,
      positionId = "1234",
      email = Random.nextInt(10000) + "blabla@gmail.com"
    )
  }

}

class ApplicationManager(emailsManager: EmailsManager,
                         peopleManager: PeopleManager,
                         positionsManager: PositionManager,
                         database: MongoDatabase,
                        ) {

  val APPLICATION_COLLECTION = "applications"

  val logger = LogManager.getLogger(this.getClass)

  lazy val applications = {
    MongoDB.createCollectionIfNotExists(APPLICATION_COLLECTION, database)
    database.getCollection(APPLICATION_COLLECTION)
  }

  def storeApplication(application: Application) = {
    logger.info(s"Storing application ${application.id}")
    MongoDB.insertValueIntoCollection(application, applications)
  }

  def updateAllComponents(application: Application) = {
    logger.info(s"Updating all components for application ${application.id}")
    emailsManager.markEmailAsUsed(email = application.email,
      company = application.company)
    peopleManager.deletePerson(application.person)
    positionsManager.successfullyAppliedForPosition(application.positionId)
  }

  def failApplication(application: Application) = {
    logger.info(s"Failing application ${application.id}")
    positionsManager.failedToApplyToPosition(application.positionId)
  }

  def createApplication(company: String): Application = {
    val person = peopleManager.getRandomPerson().get
    val email = emailsManager.getNotUsedEmail(company).get
    val position = positionsManager.getRandomPosition(company)
    Application(
      person = person,
      company = company,
      positionUrl = position.url,
      positionId = position.id,
      email = email
    )
  }


}
