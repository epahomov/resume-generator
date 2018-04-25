package resumes.response.result

import resumes.emails.MessageParser.Message
import resumes.response.ResponseCollector.Decision
import resumes.response.ResponseCollector.Decision.Decision

abstract class SimpleResultIdentifier extends ResultIdentifier {

  val ACCEPTED_STRINGS: List[String]
  val DECLINED_STRINGS: List[String]

  private lazy val accepted = ACCEPTED_STRINGS.map(_.toLowerCase.filter(_.isLetterOrDigit))
  private lazy val declined = DECLINED_STRINGS.map(_.toLowerCase.filter(_.isLetterOrDigit))

  def result(message: Message): Option[Decision] = {
    val text = (message.subject + " " + message.text).filter(_.isLetterOrDigit).toLowerCase
    accepted.find(str => {
      text.contains(str)
    }) match {
      case Some(_) => Some(Decision.ACCEPTED)
      case None => {
        declined.find(str => {
          text.contains(str)
        }) match {
          case Some(_) => Some(Decision.DECLINED)
          case None => None
        }
      }
    }
  }

}
