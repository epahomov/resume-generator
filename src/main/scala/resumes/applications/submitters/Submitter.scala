package resumes.applications.submitters

import java.io.{BufferedReader, File, InputStreamReader}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.{Timer, TimerTask}
import javax.imageio.ImageIO

import net.liftweb.json.Extraction.decompose
import net.liftweb.json.JsonAST.prettyRender
import org.apache.logging.log4j.LogManager
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.RemoteWebDriver
import resumes.MongoDB.formats
import resumes.applications.ApplicationManager.Application
import resumes.applications.{ApplicationManager, NumberOfApplicationsSelector}
import resumes.company.CompanyManager.Companies
import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.shooting.ShootingStrategies

import scala.util.{Failure, Success, Try}

abstract class Submitter(applicationManager: ApplicationManager,
                         numberOfApplicationsSelector: NumberOfApplicationsSelector
                        ) {

  protected val logger = LogManager.getLogger(this.getClass)

  def submit(application: Application, reallySubmit: Boolean = false): Try[Unit] = {
    System.setProperty("webdriver.gecko.driver", "/Users/macbook/Downloads/geckodriver_firefox")
    val driver = new FirefoxDriver()
    driver.manage().window().maximize()
    Thread.sleep(2000)
    val shouldKill = new AtomicBoolean(true)
    new Timer().schedule(new TimerTask() {
      override def run(): Unit = {
        if (shouldKill.get()) {
          logger.error("Took too long to submit applicaiton, killing firefox")
          val builder = new ProcessBuilder("./kill_firefox.sh")
          builder.redirectErrorStream(true)
          builder.start()
        }
      }
    }, 10L * 60L * 1000L)

    try {
      submitImpl(driver, application, reallySubmit)
      Try {
        val builder = new ProcessBuilder("./kill_firefox.sh")
        builder.redirectErrorStream(true)
        builder.start()
      }
      Success()
    } catch {
      case e: Exception => {
        if (e.getMessage.contains("Emails used")) {
          applicationManager.getEmailsManager.markEmailAsUsedForApplication(application.email, Companies.withName(application.company))
        }
        Failure(e)
      }
    } finally {
      Try {
        shouldKill.set(false)
        driver.close()
        driver.close()
        driver.close()
      }
    }
  }


  def getScreenShot(driver: RemoteWebDriver, applicationId: String): Unit = {
    try {

      val rootFolder = "/Users/macbook"
      val screenshotsDirectory = new File(s"$rootFolder/screenshots")
      if (!screenshotsDirectory.exists()) {
        screenshotsDirectory.mkdir()
      }
      val today = DateTimeFormat.forPattern("MMddyyyy").print(new DateTime)
      val screenShotFileName = s"$screenshotsDirectory/${applicationId}_$today.png"
      val screenshot = new AShot()
        .shootingStrategy(ShootingStrategies.viewportPasting(100))
        .takeScreenshot(driver)
      ImageIO.write(screenshot.getImage, "png", new File(screenShotFileName))
    } catch {
      case e: Exception => logger.error(s"Could not save screenshot for application - $applicationId", e)
    }
  }

  def submitImpl(driver: RemoteWebDriver, application: Application, reallySubmit: Boolean = false): Unit

  val company: Companies.Value

  def submitAllNecessaryApplicationsForToday() = {
    val numberOfApplications = numberOfApplicationsSelector.getNumberOfApplications(company)
    logger.info(s"Number of applications I generate for company $company = $numberOfApplications")
    val maxAttempts = numberOfApplications * 3
    var attempts = 0
    var submitted = 0

    while (submitted < numberOfApplications && attempts < maxAttempts) {
      attempts += 1
      val application = applicationManager.createApplication(company)
      logger.info(s"Company: $company, attempt: $attempts, application: \n {}", prettyRender(decompose(application)))
      submit(application, true) match {
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
