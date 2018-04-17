package resumes.response.relevance

import resumes.applications.ApplicationManager.Application
import resumes.emails.MessageParser.Message

abstract class SimpleRelevanceIdentifier extends RelevancyIdentifier {

  def isRelevant(application: Application, message: Message): Boolean = {
    message.senders.find(sender => {
      sender.toLowerCase.contains(suffix)
    }).isDefined
  }

  val suffix: String

}
