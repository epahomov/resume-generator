package resumes.applications

import resumes.MongoDB
import resumes.company.{CompanyManager, PositionManager}
import resumes.emails.{EmailServerWrapper, EmailsManager}
import resumes.response.ResponseManager

object JobRunner {
  def main(args: Array[String]): Unit = {
    val emailsManager = new EmailsManager(MongoDB.database)
    val positionManager = new PositionManager(MongoDB.database)
    val companyManager = new CompanyManager(MongoDB.database)
    val applicationManager = new ApplicationManager(emailsManager,
      positionManager,
      MongoDB.database)
//    val numberOfApplicationsSelector = new NumberOfApplicationsSelector(companyManager) {
//      override def getNumberOfApplications(company: String, current: DateTime = new DateTime()): Int = {
//        10
//      }
//    }
//    val submitter = new IBMApplicationSubmitter(applicationManager, numberOfApplicationsSelector)
//    submitter.submitAllNecessaryApplicationsForToday()
    val responseManager = new ResponseManager(applicationManager, new EmailServerWrapper)
    responseManager.collectResponses()
  }


}
