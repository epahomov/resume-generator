package resumes.emails

import java.util.Date
import javax.mail
import javax.mail.Address
import javax.mail.internet.MimeMultipart

object MessageParser {

  case class Message(
                      senders: List[String],
                      recipients: List[String],
                      subject: String,
                      date: Date,
                      text: String,
                      id: String
                    )

  def parse(message: javax.mail.Message): Message = {
    val senders = getAddresses(message, message.getFrom)
    val recipients = getAddresses(message, message.getAllRecipients)
    val subject = message.getSubject
    val date = message.getSentDate
    val text = getTextFromMessage(message)
    val id = (senders.mkString("") + text).hashCode.toString
    Message(
      senders = senders,
      recipients = recipients,
      subject = subject,
      date = date,
      text = text,
      id = id
    )
  }

  private def getTextFromMessage(message: javax.mail.Message): String = {
    var result = ""
    if (message.isMimeType("text/plain")) {
      result = message.getContent.toString
    } else if (message.isMimeType("multipart/*")) {
      val mimeMultipart = message.getContent.asInstanceOf[MimeMultipart]
      result = getTextFromMimeMultipart(mimeMultipart)
    }
    result
  }

  //https://stackoverflow.com/questions/11240368/how-to-read-text-inside-body-of-mail-using-javax-mail/34689614
  private def getTextFromMimeMultipart(mimeMultipart: MimeMultipart): String = {
    var result = ""
    val count = mimeMultipart.getCount
    var i = 0
    while ( {
      i < count
    }) {
      val bodyPart = mimeMultipart.getBodyPart(i)
      if (bodyPart.isMimeType("text/plain")) {
        result = result + "\n" + bodyPart.getContent
      }
      else if (bodyPart.isMimeType("text/html")) {
        val html = bodyPart.getContent.asInstanceOf[String]
        result = result + "\n" + org.jsoup.Jsoup.parse(html).text()
      }
      else if (bodyPart.getContent.isInstanceOf[MimeMultipart]) {
        result = result + getTextFromMimeMultipart(bodyPart.getContent.asInstanceOf[MimeMultipart])
      }
      i += 1
    }
    result
  }

  private def getAddresses(message: mail.Message, addresses: Array[Address]) = {
    addresses
      .map(address => {
        address.toString
      })
      .toList
  }
}
