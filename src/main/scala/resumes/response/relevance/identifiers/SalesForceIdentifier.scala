package resumes.response.relevance.identifiers

import resumes.applications.ApplicationManager.Application
import resumes.emails.MessageParser.Message
import resumes.response.relevance.SimpleRelevanceIdentifier

object SalesForceIdentifier extends SimpleRelevanceIdentifier {

  val suffix: String = ""

  override def isRelevant(application: Application, message: Message): Boolean = {
    false
  }
}
