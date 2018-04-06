package resumes.applications.submitters

import java.util.concurrent.TimeUnit

import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.{JavascriptExecutor, Keys, WebElement}
import org.openqa.selenium.remote.RemoteWebDriver

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object SeleniumUtils {

  def bigPause = {
    Thread.sleep(10000)
  }

  def smallPause = {
    Thread.sleep(1000)
  }

  def averagePause = {
    Thread.sleep(5000)
  }

  def getParentNode(driver: RemoteWebDriver, element: WebElement): WebElement = {
    driver.asInstanceOf[JavascriptExecutor].executeScript("return arguments[0].parentNode;", element).asInstanceOf[WebElement]
  }


  def scrollTo(driver: RemoteWebDriver, element: WebElement) = {
    driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", element)
  }

  def dropDown(driver: RemoteWebDriver, dropDown: WebElement, keys: CharSequence) = {
    runWithTimeout(() => {
      scrollTo(driver, dropDown)
      new Actions(driver)
        .moveToElement(dropDown)
        .click()
        .pause(300)
        .sendKeys(keys)
        .pause(300)
        .sendKeys(Keys.ENTER)
        .perform()

    })
    smallPause
  }

  def dropDownOffset(driver: RemoteWebDriver, element: WebElement, offset: Int) = {
    runWithTimeout(() => {
      scrollTo(driver, element)
      var action = new Actions(driver)
        .moveToElement(element)
        .click()
        .pause(1000)

      (0 to offset - 1).foreach(_ => {
        action = action.sendKeys(Keys.DOWN).pause(200)
      })
      action.sendKeys(Keys.ENTER).pause(1000).perform()
    })
    smallPause
  }


  def runWithTimeout[T](f: () => Unit) {
    import scala.concurrent.ExecutionContext.Implicits.global
    try {
      Await.result(Future {
        f()
      }, Duration.apply(100, TimeUnit.SECONDS))
    } catch {
      case e: Exception => Await.result(Future {
        f()
      }, Duration.apply(200, TimeUnit.SECONDS))
    }
  }

}
