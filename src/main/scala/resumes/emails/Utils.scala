package resumes.emails

import resumes.emails.EmailServerWrapper.Credentials

import scala.io.Source
import EmailServerWrapper._

object Utils {

  def verifyAccounts(path: String) = {
    var valid = 0
    var broken = 0

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

          val store = getYahooImapsStore(credential)
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



  //    val credentials = Credentials("emmerybeighlie@yahoo.com", "090c6ad065")
  //
  //    val store = getYahooImapsStore(credentials)
  //
  //    store.isConnected
  //
  ////    val folders = store.getDefaultFolder.list()
  ////    folders.foreach(folder => {
  ////      println(folder.getName)
  ////    })
  //    val folder = store.getFolder("Inbox")
  //    folder.open(Folder.READ_ONLY)
  //
  //    println(folder.getMessageCount)
  //    println(folder.getMessages()(0).getFrom()(0))
}
