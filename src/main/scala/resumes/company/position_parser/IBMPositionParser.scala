package resumes.company.position_parser

import net.liftweb.json.Extraction.decompose
import net.liftweb.json.JsonAST.prettyRender
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import resumes.MongoDB.formats
import resumes.Utils.normalize
import resumes.company.CompanyManager.Companies
import resumes.company.PositionManager.Area.Area
import resumes.company.PositionManager.{ExperienceLevel, Position}
import resumes.generators.education.Enums.Degree
import resumes.generators.person.AddressGenerator

import scala.collection.JavaConverters._
import scala.io.Source

class IBMPositionParser {

  private def getSkillsByArea(area: Area): List[String] = {
    val fileName = resumes.generators.Utils.areaToFileSystemName.get(area).get
    Source.fromResource(s"skills/$fileName.txt").getLines().map(_.toLowerCase).toSet.toList
  }


  def parsePosition(url: String, area: Area): Position = {
    System.setProperty("webdriver.gecko.driver", "/Users/macbook/Downloads/geckodriver_firefox")
    val driver = new FirefoxDriver()
    driver.manage().window().maximize()
    driver.get(s"https://careers.ibm.com/ShowJob/Id/$url")
    Thread.sleep(5000)
    val specification = driver
      .findElementsByCssSelector("[aria-label^='Job Specification']")
      .get(1)
      .findElement(By.tagName("ul"))
      .findElements(By.tagName("li"))
      .asScala
      .map(_.getText)
      .map(line => {
        val fieldName = line.split(":")(0)
        val value = line.split(":")(1)
        fieldName -> value
      }).toMap

    val text = driver.findElementByClassName("job-main").getText

    val (minimumRequiredDegree, minimumRequiredDegreeComment) = {
      val (normalized, comment) = specification.get("Required Education") match {
        case Some(minimumDegree) => {
          (normalize(minimumDegree), "Used specification to extract degree")
        }
        case None => {
          (normalize(text), "Used text to extract degree")
        }
      }
      if (normalized.contains(normalize(Degree.Master.toString))) {
        (Degree.Master, comment)
      } else if (normalized.contains(normalize(Degree.Bachelor.toString))) {
        (Degree.Bachelor, comment)
      } else {
        (Degree.Associate, comment)
      }
    }

    val (address, popularity) = {
      val state = specification.get("State")
      state match {
        case Some(st) => {
          if (normalize(st).contains("multiple")) {
            (Some(AddressGenerator.MULTIPLE_LOCATIONS), 50)
          } else {
            val city = specification.get("City").get
            (Some(s"$city, $st"), 10)
          }
        }
        case None => (None, 10)
      }
    }

    val (experienceLevel, experienceLevelComment) = {
      if (
        normalize(text).take(100).contains("junior") ||
          normalize(text).take(100).contains("apprentice")
      ) {
        (ExperienceLevel.Beginner, "found key word in title")
      } else if (normalize(text).take(100).contains("intern") ||
        normalize(text).take(100).contains("coop")
      ) {
        (ExperienceLevel.Freshly_Graduate, "found key word in title")
      } else if (specification.get("Position Type").isDefined && normalize(specification.get("Position Type").get).contains("intern")) {
        (ExperienceLevel.Freshly_Graduate, "used position type")
      } else if (specification.get("Contract Type").isDefined && normalize(specification.get("Contract Type").get).contains("intern")) {
        (ExperienceLevel.Freshly_Graduate, "used Contract Type")
      } else if (specification.get("Position Type").isDefined && normalize(specification.get("Position Type").get).contains("early")) {
        (ExperienceLevel.Beginner, "used position type")
      } else if (
        normalize(text).take(100).contains("senior") ||
          normalize(text).take(100).contains("architect") ||
          normalize(text).take(100).contains("manager")
      ) {
        (ExperienceLevel.Senior, "found key word in title")
      } else {
        (ExperienceLevel.Middle, "couldn't find anything")
      }
    }


    def softNormalize(string: String) = string.filter(x => x.isLetterOrDigit || x.equals('+')).toLowerCase

    val skills = {
      val normalized = softNormalize(text)
      getSkillsByArea(area).filter(skill => normalized.contains(softNormalize(skill)))
    }

    val position = Position(
      company = Companies.IBM.toString,
      url = url,
      area = Some(area.toString),
      experienceLevel = Some(experienceLevel.toString),
      popularity = Some(popularity),
      minimumDegreeNecessary = Some(minimumRequiredDegree.toString),
      address = address,
      skills = Some(skills),
      parsingComments = Some(List(minimumRequiredDegreeComment, s"Experience level: $experienceLevelComment"))
    )
    driver.close()
    println(prettyRender(decompose(position)))
    val positionWithText = position.copy(text = Some(text))
    positionWithText
  }

}
