package resumes.emails

import resumes.emails.EmailServerWrapper.Credentials

import scala.io.Source

object Utils {


  def verifyAccounts(path: String) = {
    var valid = 0
    var broken = 0
    val emailServerWrapper = new EmailServerWrapper
    def log(): Unit = {
      println(s"Broken = $broken")
      println(s"valid = $valid")
    }

    Source
      .fromResource("emails_test.txt")
      .getLines()
      .map(line => {
        val a = line.split(":")
        Credentials(a(0), a(1))
      })
      .foreach(credential => {
        try {

          val store = emailServerWrapper.getStore(credential)
          if (broken % 10 == 0) {
            log()
          }
          if (store.isConnected) {
            valid += 1
          } else {
            broken += 1
          }
        } catch {
          case e: Exception => broken += 1
        }
      })
    log()
  }

}
