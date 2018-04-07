package resumes.applications.submitters

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.{By, JavascriptExecutor, WebElement}
import resumes.applications.ApplicationManager.Application
import resumes.applications.submitters.SeleniumUtils._
import resumes.applications.{ApplicationManager, NumberOfApplicationsSelector}
import resumes.company.CompanyManager.Companies
import resumes.generators.name.FirstNameGenerator.Origin
import resumes.generators.work.EmploymentGenerator.Employment

import scala.util.Random


object SalesForceApplicationSubmitter extends SubmitterHelper {

  val submitter = new SalesForceApplicationSubmitter(null, null)

  def main(args: Array[String]): Unit = {
    almostSubmitOneDummyApplication
    //almostSubmitNotSoDummyApplication
  }

}

class SalesForceApplicationSubmitter(applicationManager: ApplicationManager,
                                     numberOfApplicationsSelector: NumberOfApplicationsSelector
                                    ) extends Submitter(applicationManager, numberOfApplicationsSelector) {
  val company = Companies.SalesForce

  def submitImpl(driver: RemoteWebDriver, application: Application, reallySubmit: Boolean = false): Unit = {
    val url = application.positionUrl
    logger.info(s"Applying for position - ${url}")
    driver.get(url)
    bigPause
    driver.findElementById("wd-DropDownCommandButton-commandButton.siteLabels['POSTING.Apply_Button']").click()
    bigPause
    driver.findElementByClassName("WPIU").click()
    bigPause
    driver.findElementByClassName("WJ2L").findElement(By.tagName("input")).sendKeys(application.email)
    smallPause
    driver.findElementsByClassName("WJ2L").get(1).findElement(By.tagName("input")).sendKeys(application.passwordToAccount)
    smallPause
    driver.findElementsByClassName("WJ2L").get(2).findElement(By.tagName("input")).sendKeys(application.passwordToAccount)
    smallPause
    driver.findElementByClassName("WP-S").click()
    bigPause
    nextPage(driver)
    driver.findElementById("textInput.nameComponent--uid43-input").sendKeys(application.person.name.firstName)
    driver.findElementById("textInput.nameComponent--uid44-input").sendKeys(application.person.name.lastName)
    driver.findElementById("textInput.addressComponentsDeferred[i]--uid46-input")
      .sendKeys(application.person.address.street + " " + application.person.address.house)
    driver.findElementById("textInput.addressComponentsDeferred[i]--uid47-input")
      .sendKeys(application.person.address.city)
    driver.findElementById("textInput.addressComponentsDeferred[i]--uid49-input")
      .sendKeys(application.person.address.zipCode)
    driver.findElementById("textInput.phone--uid53-input")
      .sendKeys(application.person.phoneNumber)

    val element = driver.findElementById("dropDownSelectList.sources-input--uid54-input")
    val source = getSource()
    logger.info(s"Source - $source")
    dropDown(driver, element, source)

    nextPage(driver)

    var index = 0
    def add(index: Int): WebElement = {
      driver.findElementsByCssSelector("[title=Add]").get(index)
    }
    application.person.education.foreach(education => {

      scrollTo(driver, add(1))
      getParentNode(driver, add(1)).click()
      averagePause
      driver.findElements(By.cssSelector("""[id^=textInput\.schoolName]""")).get(2 + index * 3).sendKeys(education.university.name)
      val degreeElement = driver.findElements(By.cssSelector("""[id^=dropDownSelectList\.schoolDegrees""")).get(1 + index * 2)
      val firstLetterOfDegree = education.degree.head
      dropDown(driver, degreeElement, firstLetterOfDegree.toString)
      val majorElement = driver.findElementsByClassName("WGKX").get(0 + index)
      dropDown(driver, majorElement, education.major.get)

      driver.findElements(By.cssSelector("""[id^=textInput\.schoolGpa""")).get(2 + index * 3).sendKeys(education.GPA.get.toString)
      driver.findElements(By.cssSelector("""[id^=dateInput\.educationStartDate""")).get(2 + index * 3).sendKeys(education.startYear.toString)
      driver.findElements(By.cssSelector("""[id^=dateInput\.educationEndDate""")).get(2 + index * 3).sendKeys(education.endYear.toString)
      index += 1
      smallPause
    })

    index = 0
    application.person.workExperience.foreach(employment => {
      scrollTo(driver, add(0))
      getParentNode(driver, add(0)).click()
      averagePause
      driver.findElements(By.cssSelector("""[id^=textInput\.jobHistoryTitle]""")).get(2 + index * 3).sendKeys(employment.role)
      driver.findElements(By.cssSelector("""[id^=textInput\.jobHistoryCompany]""")).get(2 + index * 3).sendKeys(employment.company)
      driver.findElements(By.cssSelector("""[id^=dateInput\.jobStartDate]""")).get(2 + index * 3).sendKeys(DateTimeFormat.forPattern("MMyyyy").print(new DateTime(employment.start)))
      driver.findElements(By.cssSelector("""[id^=dateInput\.jobEndDate]""")).get(2 + index * 3).sendKeys(DateTimeFormat.forPattern("MMyyyy").print(new DateTime(employment.end)))
      index += 1
    })

    nextPage(driver)

    var drops = driver.findElements(By.cssSelector("""[id^=dropDownSelectList"""))
    dropDown(driver, drops.get(1), "y")
    dropDown(driver, drops.get(3), "y")
    dropDown(driver, drops.get(5), "n")
    dropDown(driver, drops.get(7), "n")

    nextPage(driver)

    drops = driver.findElements(By.cssSelector("""[id^=dropDownSelectList"""))

    dropDown(driver, drops.get(1), application.person.gender.toString.head.toString)

    dropDownOffset(driver, drops.get(3), 3)

    smallPause
    val offset = application.person.origin match {
      case Origin.US => 8
      case Origin.India => 2
    }
    dropDownOffset(driver, drops.get(5), offset)

    val checkbox = driver.findElements(By.cssSelector("""[id^=checkBoxInput""")).get(0)
    scrollTo(driver, checkbox)
    checkbox.click()

    nextPage(driver)

    driver.findElements(By.cssSelector("""[id^=textInput\.nameEnglish""")).get(2).sendKeys(application.person.name.firstName + " " + application.person.name.lastName)
    val today = DateTimeFormat.forPattern("MMddyyyy").print(new DateTime)
    logger.info("Today is = " + today)
    driver.findElements(By.cssSelector("""[id^=dateInput\.todaysDateEnglish""")).get(2).sendKeys(today)
    averagePause

    driver.findElementsByClassName("WJGF").get(1).click()

    nextPage(driver)

    if (reallySubmit) {
      getScreenShot(driver, application.id)
      driver.findElementsByCssSelector("[title=Submit]").get(1).click()
    }
    bigPause
  }




  private def nextPage(driver: RemoteWebDriver) = {
    smallPause
    driver.findElementsByCssSelector("[title=Next]").get(1).click()
    bigPause
  }


  def getSource() = {
    val options = List("s", "t", "u", "v", "x", "j", "l", "i")
    options(Random.nextInt(options.size) - 1)
  }

}
