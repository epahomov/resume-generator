package resumes.applications

import java.util.{Date, UUID}

import resumes.applications.ApplicationManager.Application
import resumes.company.PositionManager
import resumes.emails.EmailsManager
import resumes.generators.PeopleManager
import resumes.generators.PeopleManager.Person

object ApplicationManager {

  case class Application(person: Person,
                         company: String,
                         positionUrl: String,
                         email: String,
                         id: String = UUID.randomUUID().toString,
                         date: Date = new Date()
                        )

}

class ApplicationManager(emailsManager: EmailsManager,
                         peopleManager: PeopleManager,
                         positionsManager: PositionManager
                        ) {

  def createApplication(company: String): Application = {
    val person = peopleManager.getRandomPerson().get
    val email = emailsManager.getNotUsedEmail(company).get
    val position = positionsManager.getRandomPosition(company).url
    Application(
      person,
      company,
      position,
      email
    )
  }


}
