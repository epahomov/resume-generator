package resumes.applications.submitters

import net.liftweb.json.Extraction.decompose
import net.liftweb.json.JsonAST.prettyRender
import org.apache.logging.log4j.LogManager
import resumes.MongoDB.formats
import resumes.applications.ApplicationManager.Application
import resumes.applications.{ApplicationManager, NumberOfApplicationsSelector}
import resumes.company.CompanyManager.Companies

import scala.util.{Failure, Success, Try}

abstract class Submitter(applicationManager: ApplicationManager,
                         numberOfApplicationsSelector: NumberOfApplicationsSelector
                        ) {

  def submit(application: Application): Try[Unit]

  val company: Companies.Value

  val logger = LogManager.getLogger(this.getClass)

  def submitAllNecessaryApplicationsForToday() = {
    val numberOfApplications = numberOfApplicationsSelector.getNumberOfApplications(company)
    logger.info(s"Number of applications I generate for company $company = $numberOfApplications")
    val maxAttempts = numberOfApplications * 3
    var attempts = 0
    var submitted = 0

    while (submitted < numberOfApplications && attempts < maxAttempts) {
      attempts += 1
      val application = applicationManager.createApplication(company)
      logger.info(s"Company: $company, attempt: $attempts, application: \n {}" , prettyRender(decompose(application)))
      submit(application) match {
        case Success(_) => {
          logger.info(s"Application ${application.id} was submitted successfully")
          applicationManager.storeApplication(application)
          applicationManager.updateAllComponents(application)
          submitted += 1
        }
        case Failure(e) => {
          logger.error(s"Could not process application ${application.id}", e)
          applicationManager.failApplication(application)
        }
      }
    }
  }


}
