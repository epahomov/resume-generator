package resumes.applications.submitters

import org.apache.commons.lang3.RandomStringUtils
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.{By, Keys}
import resumes.MongoDB
import resumes.applications.ApplicationManager.Application
import resumes.applications.{ApplicationManager, NumberOfApplicationsSelector, Submitter}
import resumes.company.PositionManager
import resumes.generators.name.FirstNameGenerator.{Gender, Origin}

import scala.util.{Success, Try}

object IBMApplicationSubmitter {

  def main(args: Array[String]): Unit = {
    val submitter = new IBMApplicationSubmitter(null, null)

    val positionManager = new PositionManager(MongoDB.database)

    (0 to 15).foreach(_ => {
      val position = positionManager.getRandomPosition("ibm")
      submitter.submit(ApplicationManager.veryPlainApplication("ibm", position.url))
    })
  }

}

class IBMApplicationSubmitter(applicationManager: ApplicationManager,
                              numberOfApplicationsSelector: NumberOfApplicationsSelector
                             ) extends Submitter(applicationManager, numberOfApplicationsSelector) {

  val company = "ibm"

  def submit(application: Application): Try[Unit] = {
    try {
      System.setProperty("webdriver.gecko.driver", "/Users/macbook/Downloads/geckodriver")
      val driver = new FirefoxDriver()
      driver.manage().window().maximize()
      val url = s"https://careers.ibm.com/ShowJob/Id/${application.positionUrl}"
      logger.info(s"Applying for position - ${url}")
      driver.get(url)
      Thread.sleep(5000)
      if (driver.findElementsByClassName("applyBtnTopDiv").size() == 0) {
        throw new RuntimeException("Page does not have contain button anymore")
      }
      driver.findElementByClassName("applyBtnTopDiv").click()
      Thread.sleep(10000)
      val iterator = driver.getWindowHandles.iterator()
      iterator.next()
      driver.switchTo().window(iterator.next())
      Thread.sleep(7000)
      val formButton = driver.findElementsById("formCtrl_cmd1")
      if (formButton.size() == 0) {
        throw new RuntimeException("Page does not have contain button anymore")
      }
      formButton.get(0).click()
      Thread.sleep(5000)
      val a_s = driver.findElementByName("signInForm").findElements(By.tagName("a"))
      a_s.get(a_s.size() - 1).click()
      Thread.sleep(3000)
      driver.findElementByTagName("button").click()
      driver.findElementById("username").sendKeys(application.email)
      driver.findElementById("password").sendKeys(application.passwordToAccount)
      driver.findElementById("confirmPassword").sendKeys(application.passwordToAccount)
      driver.findElementById("selectSecurityQuestion1-button_text").click()
      Thread.sleep(2000)
      driver.findElementById("selectSecurityQuestion1-menu").sendKeys(Keys.DOWN)
      driver.findElementById("selectSecurityQuestion1-menu").sendKeys(Keys.DOWN)
      driver.findElementById("selectSecurityQuestion1-menu").sendKeys(Keys.ENTER)
      driver.findElementById("securityQuestion1Answer").sendKeys(RandomStringUtils.randomAlphanumeric(8, 12))
      driver.findElementById("createAccountForm_BUTTON_0").click()
      Thread.sleep(5000)
      driver.findElementById("startapply").click()
      Thread.sleep(5000)
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
        driver.findElementById(s"edumajor0").sendKeys(education.major)
        Thread.sleep(3000)
      })

      Thread.sleep(1000)
      driver.findElementById(s"shownext").click()
      Thread.sleep(5000)
      dropDown("United Stat", s"custom_6140_32_fname_slt_0_6140-input", driver)
      Thread.sleep(1000)
      driver.findElementById(s"radio-7384-Yes").click()
      driver.findElementById(s"radio-7385-No").click()
      if (application.person.gender.equals(Gender.Male)) {
        driver.findElementById(s"radio-6306-OptFemale").click()
      } else {
        driver.findElementById(s"radio-6306-OptMale").click()
      }
      dropDown("Non", s"custom_6307_33_fname_slt_0_6307-button_text", driver)


      if (application.person.origin.equals(Origin.US)) {
        dropDown(7, "custom_6561_33_fname_slt_0_6561-button_text", driver)
      } else {
        dropDown(2, "custom_6561_33_fname_slt_0_6561-button_text", driver)
      }

      dropDown(1, "custom_6582_33_fname_slt_0_6582-button_text", driver)
      dropDown(1, "custom_6554_33_fname_slt_0_6554-button_text", driver)
      driver.findElementById(s"radio-6629-N").click()
      driver.findElementById(s"custom_6630_33_fname_txt_0").sendKeys(application.person.name.firstName + " " + application.person.name.lastName)
      driver.findElementById(s"buildResume").click()
      driver.findElementById(s"shownext").click()
      Thread.sleep(3000)
      driver.findElementById(s"checkbox-10885-Iagree").click()
      Thread.sleep(2000)
      driver.findElementById(s"shownext").click()
      //driver.findElementById(s"save").click()

    } catch {
      case e: Throwable => e.printStackTrace()
    }
    Success()
  }

  private def dropDown(offset: Int, parameter: String, driver: FirefoxDriver): Unit = {
    val element = driver.findElementById(parameter)
    import org.openqa.selenium.JavascriptExecutor
    driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", element)
    Thread.sleep(500)
    var action = new Actions(driver)
      .moveToElement(element)
      .click()
    (0 to offset - 1).foreach(_ => {
      action = action.sendKeys(Keys.DOWN)
    })
    action.sendKeys(Keys.ENTER)
      .perform()
    Thread.sleep(2000)
  }

  private def dropDown(value: String, parameter: String, driver: FirefoxDriver) = {
    val element = driver.findElementById(parameter)
    import org.openqa.selenium.JavascriptExecutor
    driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", element)
    Thread.sleep(500)
    new Actions(driver)
      .moveToElement(element)
      .click()
      .sendKeys(value)
      .perform()
    Thread.sleep(1500)
    new Actions(driver)
      .moveToElement(element)
      .sendKeys(Keys.ENTER)
      .sendKeys(Keys.DOWN)
      .sendKeys(Keys.ENTER)
      .perform()
    Thread.sleep(2000)
  }
}
