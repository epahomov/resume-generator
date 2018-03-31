package resumes.response.relevance

import resumes.applications.ApplicationManager.Application
import resumes.emails.MessageParser.Message

abstract class RelevancyIdentifier {

  def isRelevant(application: Application, message: Message): Boolean

}
