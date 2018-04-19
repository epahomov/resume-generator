package resumes.company.position_parser

import org.openqa.selenium.{By, WebElement}
import org.openqa.selenium.firefox.FirefoxDriver
import resumes.company.PositionManager.Area
import resumes.company.PositionManager.Area.Area

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

object IBMPositionListParser {

  val areaToUrl = Map(
    Area.Computer_Science -> "https://careers.ibm.com/ListJobs/All/Search/primary-job-category/software-development---support/country/us/sortasc-state/Page-",
    Area.Hardware -> "https://careers.ibm.com/ListJobs/All/search/primary-job-category/hardware-development---support/country/us/sortasc-state/Page-"
  )

  def getUrls(): List[(Area, String)] = {
    System.setProperty("webdriver.gecko.driver", "/Users/macbook/Downloads/geckodriver_firefox")
    val driver = new FirefoxDriver()
    driver.manage().window().maximize()


    val result = areaToUrl.keys.toList.flatMap(area => {
      val urlBase = areaToUrl.get(area).get
      val result = ListBuffer[String]()
      var pageNumber = 1
      driver.get(urlBase + pageNumber)
      Thread.sleep(5000)
      var rows: List[WebElement] = null

      def updateRows(): Unit = {
        rows = driver
          .findElementsByCssSelector("[aria-label^='Job Results Table']")
          .asScala
          .last
          .findElement(By.tagName("tbody"))
          .findElements(By.tagName("tr"))
          .asScala.toList
      }

      def updateResultWithRows(): Unit = {
        result ++= rows.map(row => {
          row
            .findElement(By.tagName("td"))
            .findElement(By.tagName("a"))
            .getAttribute("href")
            .split("/")(5)
        })
      }

      updateRows()
      while (rows.size > 0) {
        updateResultWithRows()
        pageNumber += 1
        driver.get(urlBase + pageNumber)
        Thread.sleep(5000)
        updateRows()
      }
      result.map(url => (area, url)).toList
    })
    driver.close()
    result
  }
}
