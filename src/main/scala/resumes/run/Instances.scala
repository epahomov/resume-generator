package resumes.run

import resumes.MongoDB
import resumes.applications.ApplicationManager
import resumes.company.{CompanyManager, PositionManager}
import resumes.emails.{EmailServerWrapper, EmailsManager}
import resumes.response.ResponseManager

object Instances {
  lazy val emailsManager = new EmailsManager(MongoDB.database)
  lazy val positionManager = new PositionManager(MongoDB.database)
  lazy val companyManager = new CompanyManager(MongoDB.database)
  lazy val applicationManager = new ApplicationManager(
    emailsManager,
    positionManager,
    companyManager,
    MongoDB.database)
  lazy val responseManager = new ResponseManager(applicationManager, new EmailServerWrapper)




}
