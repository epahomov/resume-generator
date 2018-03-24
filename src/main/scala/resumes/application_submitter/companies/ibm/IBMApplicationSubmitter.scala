package resumes.application_submitter.companies.ibm

import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.{By, Keys}
import resumes.application_submitter.{Application, Submitter}
import resumes.generators.PersonGenerator

import scala.io.Source
import scala.util.Random

object IBMApplicationSubmitter extends Submitter {


  def submit(application: Application) = {
    System.setProperty("webdriver.gecko.driver", "/Users/macbook/Downloads/geckodriver")
   // System.setProperty("webdriver.chrome.driver", "/usr/local/Cellar/chromedriver/2.36/bin/chromedriver")
    val driver = new FirefoxDriver()
    driver.manage().window().maximize()
    driver.get(s"https://careers.ibm.com/ShowJob/Id/${application.positionId}")
    Thread.sleep(5000)
    driver.findElementByClassName("applyBtnTopDiv").click()
    Thread.sleep(10000)
    val iterator = driver.getWindowHandles.iterator()
    iterator.next()
    driver.switchTo().window(iterator.next())
    Thread.sleep(5000)
    driver.findElementById("formCtrl_cmd1").click()
    Thread.sleep(5000)
    val a_s = driver.findElementByName("signInForm").findElements(By.tagName("a"))
    a_s.get(a_s.size() - 1).click()
    Thread.sleep(3000)
    driver.findElementByTagName("button").click()
    val email = s"sfsdfscdfsd${Random.nextInt(1000)}@gmail.com"
    driver.findElementById("username").sendKeys(email)
    val password = "Hsdsfsdf12!"
    driver.findElementById("password").sendKeys(password)
    driver.findElementById("confirmPassword").sendKeys(password)
    driver.findElementById("selectSecurityQuestion1-button_text").click()
    Thread.sleep(2000)
    driver.findElementById("selectSecurityQuestion1-menu").sendKeys(Keys.DOWN)
    driver.findElementById("selectSecurityQuestion1-menu").sendKeys(Keys.DOWN)
    driver.findElementById("selectSecurityQuestion1-menu").sendKeys(Keys.ENTER)
    driver.findElementById("securityQuestion1Answer").sendKeys("Hisdfsdf")
    driver.findElementById("createAccountForm_BUTTON_0").click()
    Thread.sleep(5000)
    driver.findElementById("startapply").click()
    Thread.sleep(5000)
    driver.findElementById("profile_1_0_firstname_txt_0").sendKeys("legal_first_name")
    driver.findElementById("profile_3_0_lastname_txt_0").sendKeys("legal_last_name")
    driver.findElementById("profile_5_0_address1_txt_0").sendKeys("address_line_1")
    new Actions(driver)
      .moveToElement(driver.findElementById("profile_9_0_country_slt_0_0-input"))
      .click()
      .sendKeys("United Stat")
      .sendKeys(Keys.ENTER)
      .pause(1000)
      .sendKeys(Keys.DOWN)
      .sendKeys(Keys.ENTER)
      .perform()
    println("Hi")
  }

  def main(args: Array[String]): Unit = {
    val str: String = Source
      .fromFile("/Users/macbook/IdeaProjects/gmailaccountcreeator/src/main/resources/applications/ibm/candidates/0a5fc422-f246-42a3-ab14-61a5a5c652f9.json")
      .getLines()
      .mkString("\n")
    println(str)
    val candidate = PersonGenerator.deserializeCandidate(str)
    val application = Application(candidate, "174055")
    submit(application)
  }
}
