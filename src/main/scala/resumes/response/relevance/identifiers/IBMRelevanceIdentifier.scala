package resumes.response.relevance.identifiers

import resumes.applications.ApplicationManager.Application
import resumes.emails.MessageParser.Message
import resumes.response.relevance.SimpleRelevanceIdentifier

object IBMRelevanceIdentifier extends SimpleRelevanceIdentifier {
  val suffix: String = "ibm.com"

  override def isRelevant(application: Application, message: Message): Boolean = {
    super.isRelevant(application, message) &&
      !message.text.contains("interested to hear about your experience") &&
      !message.text.contains("Thank you for applying to IBM. We're excited")
  }
}
