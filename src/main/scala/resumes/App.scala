package resumes

import org.openqa.selenium.firefox.FirefoxDriver

object App {
  def main(args: Array[String]): Unit = {
    System.setProperty("webdriver.gecko.driver", "/Users/macbook/Downloads/geckodriver")
    val driver = new FirefoxDriver
    driver.get("https://accounts.google.com/SignUp?hl=en")
    val firstName = driver.findElementById("FirstName")
    val lastName = driver.findElementById("LastName")
    val gmailAddresss = driver.findElementById("GmailAddress")
    val passwd = driver.findElementById("Passwd")
    val passwdAgain = driver.findElementById("PasswdAgain")
    val birthMonth = driver.findElementById("HiddenBirthMonth")
    val birthDay = driver.findElementById("BirthDay")
    val birthYear = driver.findElementById("BirthYear")
    val gender = driver.findElementById("HiddenGender")
    val iAgree = driver.findElementById("iagreebutton")
  }

}
