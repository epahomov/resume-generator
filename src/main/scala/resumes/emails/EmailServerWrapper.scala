package resumes.emails

import java.util.Properties
import javax.mail.{Folder, Session, Store}

import resumes.MongoDB

import scala.util.Try

object EmailServerWrapper {

  case class Credentials(login: String, psswd: String)

  private def getImapsStore(credentials: Credentials, host: String) = {
    val port = "993"
    val propvls = new Properties
    val session = Session.getDefaultInstance(propvls)
    val store = session.getStore("imaps")
    store.connect(host, port.toInt, credentials.login, credentials.psswd)
    store
  }

  private def getYahooImapsStore(credentials: Credentials) = {
    getImapsStore(credentials, "imap.mail.yahoo.com")
  }

  def getStore(credentials: Credentials) = {
    if (credentials.login.endsWith("yahoo.com")) {
      getYahooImapsStore(credentials)
    } else {
      throw new RuntimeException("Does not support this email")
    }
  }

  private def getAllFolders(store: Store): List[String] = {
    val folders: Array[Folder] = store.getDefaultFolder().list()
    folders.map(_.getName).toList
  }

  private def tryGetAllMessages(credentials: Credentials): List[MessageParser.Message] = {
    val store = getStore(credentials)
    val folders = getAllFolders(store)
    val messages = folders.flatMap(folderName => {
      val folder = store.getFolder(folderName)
      folder.open(Folder.READ_ONLY)
      val m = folder.getMessages
      folder.close(false)
      m
    })
    store.close()
    messages.map(MessageParser.parse)
  }

  def getAllMessages(credentials: Credentials, tryAttempts: Int = 3): Try[List[MessageParser.Message]] = {
    var result: Try[List[MessageParser.Message]] = null
    var attempt = 0
    while ((result == null || result.isFailure) && (attempt < tryAttempts)) {
      attempt += 1
      result = Try {
        tryGetAllMessages(credentials)
      }
    }
    result
  }

  def main(args: Array[String]): Unit = {

    val email = "josephaulam@yahoo.com"
    val password = new EmailsManager(MongoDB.database).getPassword(email)
    val messages = getAllMessages(Credentials(email, password)).get
    val jsons = resumes.Utils.toJsons(messages)
    println(jsons.mkString("\n"))
  }


}
