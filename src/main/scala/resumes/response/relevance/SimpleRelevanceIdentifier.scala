package resumes.response.relevance

import resumes.applications.ApplicationManager.Application
import resumes.emails.MessageParser.Message

abstract class SimpleRelevanceIdentifier extends RelevancyIdentifier {

  def isRelevant(application: Application, message: Message) = {
    message.senders.find(sender => {
      sender.endsWith(suffix)
    })
  }

  val suffix: String

}
