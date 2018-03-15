import javax.mail._
import java.util.Properties

val propvls = new Properties()
val host = "imap-mail.outlook.com"
val port = 993
val session = Session.getDefaultInstance(propvls)
val store = session.getStore("imaps")
store.connect(host, port, "ashlyg0f0trend@hotmail.com", "kC9m15l5nA")
val folder = store.getDefaultFolder
folder.open(Folder.READ_ONLY)
folder.getMessageCount



