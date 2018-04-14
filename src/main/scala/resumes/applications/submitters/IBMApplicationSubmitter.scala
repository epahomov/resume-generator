package resumes.applications.submitters

import org.apache.commons.lang3.RandomStringUtils
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.{By, Keys}
import resumes.applications.ApplicationManager.Application
import resumes.applications.submitters.SeleniumUtils._
import resumes.applications.{ApplicationManager, NumberOfApplicationsSelector}
import resumes.company.CompanyManager.Companies
import resumes.generators.name.FirstNameGenerator.{Gender, Origin}

import scala.util.Random

object IBMApplicationSubmitter extends SubmitterHelper {

  val submitter = new IBMApplicationSubmitter(null, null)

  def main(args: Array[String]): Unit = {
    almostSubmitOneDummyApplication
    //almostSubmitNotSoDummyApplication
  }

}

class IBMApplicationSubmitter(applicationManager: ApplicationManager,
                              numberOfApplicationsSelector: NumberOfApplicationsSelector
                             ) extends Submitter(applicationManager, numberOfApplicationsSelector) {

  val company = Companies.IBM

  def submitImpl(driver: RemoteWebDriver, application: Application, reallySubmit: Boolean = false): Unit = {
    val url = s"https://careers.ibm.com/ShowJob/Id/${application.positionUrl}"
    logger.info(s"Applying for position - ${url}")
    driver.get(url)
    bigPause
    if (driver.findElementsByClassName("applyBtnTopDiv").size() == 0) {
      throw new RuntimeException("Page does not have contain button anymore")
    }
    driver.findElementByClassName("applyBtnTopDiv").click()
    bigPause
    val iterator = driver.getWindowHandles.iterator()
    iterator.next()
    driver.switchTo().window(iterator.next())
    bigPause
    val formButton = driver.findElementsById("formCtrl_cmd1")
    if (formButton.size() == 0) {
      throw new RuntimeException("Page does not have contain button anymore")
    }
    formButton.get(0).click()
    averagePause
    driver.findElementByCssSelector("[class^=newAccnt]").click()
    averagePause
    driver.findElementByTagName("button").click()
    driver.findElementById("username").sendKeys(application.email)
    driver.findElementById("password").sendKeys(application.passwordToAccount)
    driver.findElementById("confirmPassword").sendKeys(application.passwordToAccount)
    driver.findElementById("selectSecurityQuestion1-button_text").click()
    averagePause
    driver.findElementById("selectSecurityQuestion1-menu").sendKeys(Keys.DOWN)
    driver.findElementById("selectSecurityQuestion1-menu").sendKeys(Keys.DOWN)
    driver.findElementById("selectSecurityQuestion1-menu").sendKeys(Keys.ENTER)
    driver.findElementById("securityQuestion1Answer").sendKeys(RandomStringUtils.randomAlphanumeric(8, 12))
    driver.findElementById("createAccountForm_BUTTON_0").click()
    bigPause
    driver.findElementById("startapply").click()
    bigPause
    driver.findElementById("profile_1_0_firstname_txt_0").sendKeys(application.person.name.firstName)
    driver.findElementById("profile_3_0_lastname_txt_0").sendKeys(application.person.name.firstName)
    driver.findElementById("profile_5_0_address1_txt_0").sendKeys(application.person.address.street + " " + application.person.address.house)
    dropDown("United Stat", "profile_9_0_country_slt_0_0-input", driver)
    dropDown(application.person.address.stateFullName, "profile_10_0_state_slt_0_0-input", driver)
    driver.findElementById("profile_7_0_city_txt_0").sendKeys(application.person.address.city)
    driver.findElementById("profile_8_0_zip_txt_0").sendKeys(application.person.address.zipCode)
    driver.findElementById("profile_13_0_cellphone_txt_0").sendKeys(application.person.phoneNumber)
    driver.findElementById("profile_4_0_email_eml_0").sendKeys(application.email)

    var first = true
    application.person.education.map(education => {
      driver.findElementById("addEdu").click()
      if (first) {
        first = false
        driver.findElementById("chkrecent0").click()
      }
      dropDown(education.university.name, s"education_0_0_schoolname_slt_0-input", driver)
      dropDown(education.degree, s"education_0_0_degree_slt_0-input", driver)
      driver.findElementById(s"edumajor0").sendKeys(education.major.getOrElse("Computer science"))
      smallPause
    })
    var firstExp = true
    application.person.workExperience.map(workExperience => {
      def addExperience() = {
        driver.findElementById("addExp")
      }

      scrollTo(driver, addExperience())
      addExperience.click()

      def empName() = {
        driver.findElementById("empname0")
      }

      scrollTo(driver, empName())
      empName.sendKeys(workExperience.company)
      driver.findElementById("jobtitle0").sendKeys(workExperience.role)
      driver.findElementById("startyear0").sendKeys((workExperience.start.getYear + 1900).toString)
      driver.findElementById("endyear0").sendKeys((workExperience.end.getYear + 1900).toString)
      if (firstExp) {
        firstExp = false
        driver.findElementById("chkexprecent0").click()
      }
    })

    averagePause
    driver.findElementById(s"shownext").click()
    bigPause
    dropDown("United Stat", s"custom_6140_32_fname_slt_0_6140-input", driver)
    driver.findElementById(s"radio-7384-Yes").click()
    driver.findElementById(s"radio-7385-No").click()
    if (application.person.gender.equals(Gender.Male)) {
      driver.findElementById(s"radio-6306-OptFemale").click()
    } else {
      driver.findElementById(s"radio-6306-OptMale").click()
    }
    dropDown("N", s"custom_6307_33_fname_slt_0_6307-button_text", driver)


    if (application.person.origin.equals(Origin.US)) {
      dropDownOffset(driver, driver.findElementById("custom_6561_33_fname_slt_0_6561-button_text"), 7)
    } else {
      dropDownOffset(driver, driver.findElementById("custom_6561_33_fname_slt_0_6561-button_text"), 2)
    }

    dropDownOffset(driver, driver.findElementById("custom_6582_33_fname_slt_0_6582-button_text"), 1)
    dropDownOffset(driver, driver.findElementById("custom_6554_33_fname_slt_0_6554-button_text"), 1)
    driver.findElementById(s"radio-6629-N").click()
    driver.findElementById(s"custom_6630_33_fname_txt_0").sendKeys(application.person.name.firstName + " " + application.person.name.lastName)
    driver.findElementById(s"buildResume").click()
    dropDownOffset(driver, driver.findElementById("custom_6148_32_fname_slt_0_6148-button"), Random.nextInt(6) + 1)
    driver.findElementById(s"shownext").click()
    bigPause
    driver.findElementById(s"checkbox-10885-Iagree").click()
    smallPause
    driver.findElementById(s"shownext").click()
    bigPause
    if (reallySubmit) {
      getScreenShot(driver, application.id)
      driver.findElementById(s"save").click()
    }
    bigPause
  }

  private def dropDown(value: String, parameter: String, driver: RemoteWebDriver) = {
    runWithTimeout(() => {
      val element = driver.findElementById(parameter)
      scrollTo(driver, element)
      Thread.sleep(500)
      new Actions(driver)
        .moveToElement(element)
        .click()
        .sendKeys(value)
        .perform()
      smallPause
      new Actions(driver)
        .moveToElement(element)
        .sendKeys(Keys.ENTER)
        .pause(300)
        .sendKeys(Keys.DOWN)
        .pause(300)
        .sendKeys(Keys.ENTER)
        .perform()
    })
    smallPause
  }
}
