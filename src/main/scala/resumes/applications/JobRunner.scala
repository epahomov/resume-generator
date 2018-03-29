package resumes.applications

import org.joda.time.DateTime
import resumes.MongoDB
import resumes.applications.submitters.IBMApplicationSubmitter
import resumes.company.{CompanyManager, PositionManager}
import resumes.emails.EmailsManager
import resumes.generators.PeopleManager

object JobRunner {
  def main(args: Array[String]): Unit = {
    val emailsManager = new EmailsManager(MongoDB.database)
    val positionManager = new PositionManager(MongoDB.database)
    val peopleManager = new PeopleManager(MongoDB.database)
    val companyManager = new CompanyManager(MongoDB.database)
    val applicationManager = new ApplicationManager(emailsManager, peopleManager, positionManager, MongoDB.database)
    val numberOfApplicationsSelector = new NumberOfApplicationsSelector(companyManager) {
      override def getNumberOfApplications(company: String, current: DateTime = new DateTime()): Int = {
        10
      }
    }
    val submitter = new IBMApplicationSubmitter(applicationManager, numberOfApplicationsSelector)
    submitter.submitAllNecessaryApplicationsForToday()
  }
}
