package resumes.applications

import resumes.applications.ApplicationManager.Application

import scala.util.Try

abstract class Submitter(applicationManager: ApplicationManager,
                         numberOfApplicationsSelector: NumberOfApplicationsSelector
                        ) {

  def submit(application: Application): Try[Unit]

  val company: String

  def submitAllNecessaryApplicationsForToday() = {
    val numberOfApplications = numberOfApplicationsSelector.getNumberOfApplications(company)
    val maxAttempts = numberOfApplications * 3
    var attempts = 0
    var submitted = 0

    while (submitted < numberOfApplications && attempts < maxAttempts) {
      attempts += 1
      val application = applicationManager.createApplication(company)

    }
  }


}
