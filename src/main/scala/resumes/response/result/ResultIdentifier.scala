package resumes.response.result

import resumes.emails.MessageParser.Message
import resumes.response.ResponseCollector.Decision.Decision

abstract class ResultIdentifier {

  def result(message: Message): Option[Decision]

}