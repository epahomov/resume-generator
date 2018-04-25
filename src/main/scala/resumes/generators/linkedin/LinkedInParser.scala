package resumes.generators.linkedin

import net.liftweb.json.Extraction.decompose
import net.liftweb.json.JsonAST.prettyRender
import org.apache.logging.log4j.LogManager
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.{By, JavascriptExecutor}
import resumes.MongoDB.formats
import resumes.run.Instances

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

object LinkedInParser {

  case class LinkedInPerson(
                             url: String,
                             employments: List[LinkedInEmployment],
                             education: List[LinkedInEducation],
                             name: Option[String]
                           )

  case class LinkedInEmployment(
                                 role: Option[String],
                                 company: Option[String],
                                 location: Option[String],
                                 description: Option[String],
                                 dateRange: Option[String]
                               )

  case class LinkedInEducation(
                                schoolName: Option[String],
                                time: Option[String],
                                allText: Option[String]
                              )

  protected val logger = LogManager.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    System.setProperty("webdriver.gecko.driver", "/Users/macbook/Downloads/geckodriver_firefox")
    val driver = new FirefoxDriver()
    try {
      driver.manage().window().maximize()
      driver.get("https://www.linkedin.com/")
      Thread.sleep(5000)
      driver.findElementById("login-email").sendKeys("pahomov.egor@gmail.com")
      driver.findElementById("login-password").sendKeys("lk32lk32")
      Thread.sleep(2000)
      driver.findElementById("login-submit").click()
      Thread.sleep(5000)

      var startPageNumber = 2
      (startPageNumber to 1000).foreach(pageNumber => {
        logger.info(s"startPageNumber == $startPageNumber")
        driver.get(s"https://www.linkedin.com/search/results/index/?keywords=software engineer&origin=GLOBAL_SEARCH_HEADER&page=$pageNumber")
        Thread.sleep(5000)
        val links = driver
          .findElementsByCssSelector("[class^=search-result__result-link]")
          .asScala
          .toList
          .map(webElement => {
            webElement.getAttribute("href")
          })
          .toSet
        links.foreach(link => {
          try {
            val person = parsePerson(link, driver)
            logger.info(prettyRender(decompose(person)))
            Instances.linkedInManager.uploadPerson(person)
          } catch {
            case e: Exception => logger.error(e)
          }
        })
      })
    } finally {
      driver.close()
    }
  }

  def parsePerson(url: String, driver: FirefoxDriver): LinkedInPerson = {
    logger.info(s"working with url $url")
    driver.get(url)
    Thread.sleep(10000)
    (0 to 15).foreach(_ => {
      driver.asInstanceOf[JavascriptExecutor]
        .executeScript("window.scrollBy(0, 130)")
      Thread.sleep(500)
    })
    Thread.sleep(10000)
    val li = driver
      .findElementsByCssSelector("[class^=pv-profile-section__section-info]").get(1)
      .findElements(By.tagName("li"))
      .asScala
      .toList
    val employments = li.map(webElement => {
      val role = Try {
        webElement.findElement(By.tagName("h3")).getText
      }.toOption
      val company = Try {
        webElement.findElement(By.className("pv-entity__secondary-title")).getText
      }.toOption
      val dateRange = Try {
        webElement
          .findElement(By.cssSelector("[class^=pv-entity__date-range]"))
          .findElements(By.tagName("span"))
          .get(1)
          .getText
      }.toOption
      val description = Try {
        webElement
          .findElement(By.cssSelector("[class^=pv-entity__description]"))
          .getText
      }.toOption
      val location = Try {
        webElement
          .findElement(By.cssSelector("[class^=pv-entity__location]"))
          .findElements(By.tagName("span"))
          .get(1)
          .getText
      }.toOption

      LinkedInEmployment(
        role = role,
        company = company,
        dateRange = dateRange,
        description = description,
        location = location
      )
    })

    val educationOption1 = driver
      .findElementsByCssSelector("[class^=pv-education-entity]")
      .asScala
      .toList
    val education = if (educationOption1.size > 0) {
      educationOption1.map(webElement => {
        val schoolName = Try {
          webElement.findElement(By.cssSelector("[class^=pv-entity__school-name]")).getText
        }.toOption
        val time = Try {
          webElement.findElement(By.cssSelector("[class^=pv-entity__dates]")).findElements(By.tagName("span")).get(1).getText
        }.toOption
        val allText = Try {
          webElement.getText
        }.toOption
        LinkedInEducation(
          schoolName = schoolName,
          time = time,
          allText = allText
        )
      })
    } else {
      Try {
        driver.findElementById("education-section")
      } match {
        case Success(webElement) => {
          webElement
            .findElements(By.tagName("li"))
            .asScala
            .toList
            .map(ed => {
              LinkedInEducation(
                schoolName = None,
                time = None,
                allText = Some(ed.getText)
              )
            })
        }
        case Failure(_) => List.empty
      }
    }

    val name = Try {
      driver.findElementByCssSelector("[class^=pv-top-card-section__name]").getText
    }.toOption

    LinkedInPerson(url = driver.getCurrentUrl,
      employments = employments,
      education = education,
      name = name
    )
  }
}
