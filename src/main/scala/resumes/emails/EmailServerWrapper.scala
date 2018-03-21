package resumes.emails

import java.util.Properties
import javax.mail.Session

object EmailServerWrapper {
  case class Credentials(login: String, psswd: String)

  def getImapsStore(credentials: Credentials, host: String) = {
    val port = "993"
    val propvls = new Properties
    val session = Session.getDefaultInstance(propvls)
    val store = session.getStore("imaps")
    store.connect(host, port.toInt, credentials.login, credentials.psswd)
    store
  }

  def getYahooImapsStore(credentials: Credentials) = {
    getImapsStore(credentials, "imap.mail.yahoo.com")
  }
}
