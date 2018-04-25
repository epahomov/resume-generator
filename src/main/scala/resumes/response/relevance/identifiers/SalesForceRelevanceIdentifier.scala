package resumes.response.relevance.identifiers

import resumes.applications.ApplicationManager.Application
import resumes.emails.MessageParser.Message
import resumes.response.relevance.SimpleRelevanceIdentifier

object SalesForceRelevanceIdentifier extends SimpleRelevanceIdentifier {

  val suffix: String = "salesforce"

  override def isRelevant(application: Application, message: Message): Boolean = {
    super.isRelevant(application, message) &&
      !message.text.contains("Congratulations! You have officially applied")
  }
}
