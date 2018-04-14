package resumes.applications.submitters

import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver
import resumes.applications.ApplicationManager.Application
import resumes.applications.submitters.SeleniumUtils.bigPause
import resumes.applications.{ApplicationManager, NumberOfApplicationsSelector}
import resumes.company.CompanyManager.Companies

object AmazonApplicationSubmitter extends SubmitterHelper {

  val submitter = new AmazonApplicationSubmitter(null, null)

  def main(args: Array[String]): Unit = {
    almostSubmitOneDummyApplication
    //almostSubmitNotSoDummyApplication
  }

}

class AmazonApplicationSubmitter(applicationManager: ApplicationManager,
                              numberOfApplicationsSelector: NumberOfApplicationsSelector
                             ) extends Submitter(applicationManager, numberOfApplicationsSelector) {

  val company = Companies.Amazon

  def submitImpl(driver: RemoteWebDriver, application: Application, reallySubmit: Boolean = false): Unit = {
    val url = s"https://www.amazon.jobs/en/jobs/${application.positionUrl}"
    logger.info(s"Applying for position - ${url}")
    driver.get(url)
    bigPause
    driver.findElementById("apply-button").click()
    bigPause
    driver.findElement(By.cssSelector("""[value=Register]""")).click()
    bigPause
    driver.findElementById("Registration:Form:firstName").sendKeys(application.person.name.firstName)
    driver.findElementById("Registration:Form:lastName").sendKeys(application.person.name.lastName)
    driver.findElementById("Registration:Form:email").sendKeys(application.email)
    driver.findElementById("Registration:Form:submit").click()
    bigPause
  }

}
