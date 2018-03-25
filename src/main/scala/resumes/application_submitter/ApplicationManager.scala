package resumes.application_submitter

import resumes.application_submitter.ApplicationManager.Application
import resumes.emails.EmailServerWrapper.Credentials
import resumes.emails.EmailsManager
import resumes.generators.PeopleManager
import resumes.generators.PeopleManager.Person

object ApplicationManager {

  case class Application(person: Person,
                         company: String,
                         positionId: String,
                         id: String,
                         email: Credentials
                        )

}

class ApplicationManager(emailsManager: EmailsManager,
                         peopleManager: PeopleManager
                        ) {

//  def createApplication(company: String): Application = {
//    val person = peopleManager.getRandomPerson().get
//
//  }


}
