package resumes

import java.util.Properties
import scala.io.Source

object Emails {
  def main(args: Array[String]): Unit = {
    import javax.mail._

    val host = "imap-mail.outlook.com"
    val port = "993"
    val propvls = new Properties

    case class Credentials(login: String, psswd: String)

    var valid = 0
    var broken = 0

    def log(): Unit = {
      println(s"Broken = $broken")
      println(s"valid = $valid")
    }

//    propvls.setProperty("mail.imaps.auth.plain.disable", "true")
//    propvls.put("mail.imap.starttls.enable", "true")
//    propvls.put("mail.imap.starttls.required", "true")
    val session = Session.getDefaultInstance(propvls)
    val store = session.getStore("imaps")
    store.connect(host, port.toInt, "pahomov.egor@outlook.com", "Lk32Lk32Lk32")
    println(store.isConnected)
//    Source
//      .fromResource("email_accounts.txt")
//      .getLines()
//      .map(line => {
//        val a = line.split(":")
//        Credentials(a(0), a(1))
//      })
//      .foreach(credential => {
//        try {
//          val session = Session.getDefaultInstance(propvls)
//          val store = session.getStore("imaps")
//          store.connect(host, port.toInt, credential.login, credential.psswd)
//          if (broken % 10 == 0) {
//            log()
//          }
//          if (store.isConnected) {
//            valid += 1
//          } else {
//            broken += 1
//          }
//        } catch {
//          case e: Exception => broken += 1
//        }
//      })
//    log()


  }

}
