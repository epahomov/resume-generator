package resumes.applications.submitters

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.{By, Keys, WebElement}
import resumes.MongoDB
import resumes.applications.ApplicationManager.Application
import resumes.applications.{ApplicationManager, DummyApplication, NumberOfApplicationsSelector}
import resumes.company.CompanyManager.Companies
import resumes.company.PositionManager
import resumes.company.PositionManager.{Area, Position}
import resumes.generators.name.FirstNameGenerator.Origin

import scala.util.{Failure, Random, Success, Try}


object SalesForceApplicationSubmitter {
  def main(args: Array[String]): Unit = {
    val submitter = new SalesForceApplicationSubmitter(null, null)

    val positionManager = new PositionManager(MongoDB.database)

    (0 to 15).foreach(_ => {
      val position = positionManager.getRandomPosition(Companies.SalesForce)
      println(position)
      submitter.submit(DummyApplication.veryPlainApplication("ibm", position.url))
    })
  }
}

class SalesForceApplicationSubmitter(applicationManager: ApplicationManager,
                                     numberOfApplicationsSelector: NumberOfApplicationsSelector
                                    ) extends Submitter(applicationManager, numberOfApplicationsSelector) {
  val company = Companies.SalesForce

  def submit(application: Application): Try[Unit] = {
    System.setProperty("webdriver.gecko.driver", "/Users/macbook/Downloads/geckodriver")
    val driver = new FirefoxDriver()
    try {
      driver.manage().window().maximize()
      val url = application.positionUrl
      logger.info(s"Applying for position - ${url}")
      driver.get(url)
      Thread.sleep(5000)
      driver.findElementById("wd-DropDownCommandButton-commandButton.siteLabels['POSTING.Apply_Button']").click()
      Thread.sleep(5000)
      driver.findElementByClassName("WPIU").click()
      Thread.sleep(5000)
      driver.findElementByClassName("WJ2L").findElement(By.tagName("input")).sendKeys(application.email)
      Thread.sleep(1000)
      driver.findElementsByClassName("WJ2L").get(1).findElement(By.tagName("input")).sendKeys(application.passwordToAccount)
      Thread.sleep(5000)
      driver.findElementsByClassName("WJ2L").get(2).findElement(By.tagName("input")).sendKeys(application.passwordToAccount)
      driver.findElementByClassName("WP-S").click()
      Thread.sleep(5000)
      driver.findElementByClassName("WHJI").click()
      Thread.sleep(5000)
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
      import org.openqa.selenium.JavascriptExecutor
      driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", element)
      new Actions(driver)
        .moveToElement(element)
        .click()
        .pause(1000)
        .sendKeys(source)
        .pause(1000)
        .sendKeys(Keys.ENTER)
        .perform()

      driver.findElementByClassName("WHJI").findElements(By.tagName("div")).get(1).click()
      Thread.sleep(10000)

      var index = 0
      application.person.education.foreach(education => {
        Thread.sleep(5000)
        driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", driver.findElementsByCssSelector("[title=Add]").get(1))
        driver.asInstanceOf[JavascriptExecutor].executeScript("return arguments[0].parentNode;", driver.findElementsByCssSelector("[title=Add]").get(1)).asInstanceOf[WebElement].click()
        Thread.sleep(5000)
        driver.findElements(By.cssSelector("""[id^=textInput\.schoolName]""")).get(2 + index * 3).sendKeys(education.university.name)
        val degree = driver.findElements(By.cssSelector("""[id^=dropDownSelectList\.schoolDegrees""")).get(1 + index * 2)
        val key = education.degree.head
        driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", degree)
        new Actions(driver)
          .moveToElement(degree)
          .click()
          .pause(200)
          .sendKeys(key + "")
          .pause(200)
          .sendKeys(Keys.ENTER)
          .perform()
        val major = driver.findElementsByClassName("WGKX").get(0 + index)
        driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", major)
        Thread.sleep(1000)
        new Actions(driver)
          .moveToElement(major)
          .click()
          .pause(200)
          .sendKeys(education.major.get)
          .pause(200)
          .sendKeys(Keys.ENTER)
          .perform()

        driver.findElements(By.cssSelector("""[id^=textInput\.schoolGpa""")).get(2 + index * 3).sendKeys(education.GPA.get.toString)
        driver.findElements(By.cssSelector("""[id^=dateInput\.educationStartDate""")).get(2 + index * 3).sendKeys(education.startYear.toString)
        driver.findElements(By.cssSelector("""[id^=dateInput\.educationEndDate""")).get(2 + index * 3).sendKeys(education.endYear.toString)
        index += 1
      })

      Thread.sleep(5000)
      driver.findElementsByCssSelector("[title=Next]").get(1).click()
      Thread.sleep(5000)

      var drops = driver.findElements(By.cssSelector("""[id^=dropDownSelectList"""))
      driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", drops.get(1))
      Thread.sleep(5000)
      new Actions(driver)
        .moveToElement(drops.get(1))
        .click()
        .pause(200)
        .sendKeys("y")
        .pause(200)
        .sendKeys(Keys.ENTER)
        .perform()

      driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", drops.get(3))
      new Actions(driver)
        .moveToElement(drops.get(3))
        .click()
        .pause(200)
        .sendKeys("y")
        .pause(200)
        .sendKeys(Keys.ENTER)
        .perform()

      driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", drops.get(5))
      new Actions(driver)
        .moveToElement(drops.get(5))
        .click()
        .pause(200)
        .sendKeys("n")
        .pause(200)
        .sendKeys(Keys.ENTER)
        .perform()

      driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", drops.get(7))
      new Actions(driver)
        .moveToElement(drops.get(7))
        .click()
        .pause(200)
        .sendKeys("n")
        .pause(200)
        .sendKeys(Keys.ENTER)
        .perform()

      Thread.sleep(5000)

      driver.findElementsByCssSelector("[title=Next]").get(1).click()

      Thread.sleep(5000)

      drops = driver.findElements(By.cssSelector("""[id^=dropDownSelectList"""))

      driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", drops.get(1))
      new Actions(driver)
        .moveToElement(drops.get(1))
        .click()
        .pause(200)
        .sendKeys(application.person.gender.toString.head + "")
        .pause(200)
        .sendKeys(Keys.ENTER)
        .perform()

      driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", drops.get(3))
      new Actions(driver)
        .moveToElement(drops.get(3))
        .click()
        .pause(200)
        .sendKeys(Keys.DOWN)
        .sendKeys(Keys.DOWN)
        .sendKeys(Keys.DOWN)
        .sendKeys(Keys.ENTER)
        .perform()

      Thread.sleep(1000)
      val offset = application.person.origin match {
        case Origin.US => 8
        case Origin.India => 2
      }

      driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", drops.get(5))
      var enthicity = new Actions(driver)
        .moveToElement(drops.get(5))
        .click()
        .pause(1000)

      (0 to offset - 1).foreach(_ => {
        enthicity = enthicity.sendKeys(Keys.DOWN).pause(200)
      })
      enthicity.sendKeys(Keys.ENTER).pause(1000).perform()

      val checkbox = driver.findElements(By.cssSelector("""[id^=checkBoxInput""")).get(0)
      driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", checkbox)
      checkbox.click()

      Thread.sleep(5000)

      driver.findElementsByCssSelector("[title=Next]").get(1).click()

      Thread.sleep(5000)

      driver.findElements(By.cssSelector("""[id^=textInput\.nameEnglish""")).get(2).sendKeys(application.person.name.firstName + " " + application.person.name.lastName)
      val today = DateTimeFormat.forPattern("MMddyyyy").print(new DateTime)
      logger.info("Today is = " + today)
      driver.findElements(By.cssSelector("""[id^=dateInput\.todaysDateEnglish""")).get(2).sendKeys(today)
      Thread.sleep(5000)

      driver.findElementsByClassName("WJGF").get(1).click()
      Thread.sleep(5000)

      driver.findElementsByCssSelector("[title=Next]").get(1).click()

      Thread.sleep(5000)

      driver.findElementsByCssSelector("[title=Submit]").get(1).click()
      Thread.sleep(5000)
      Success()
    } catch {
      case e: Throwable => {
        Failure(e)
      }
    } finally {
      Try {
        driver.close()
      }
    }
  }

  def getSource() = {
    val options = List("s", "t", "u", "v", "x", "j", "l", "i")
    options(Random.nextInt(options.size - 1))
  }

}
