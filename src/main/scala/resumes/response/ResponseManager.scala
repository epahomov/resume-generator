package resumes.response

import java.util.Date

import org.apache.logging.log4j.LogManager
import resumes.applications.ApplicationManager
import resumes.company.CompanyManager.Companies
import resumes.emails.EmailServerWrapper
import resumes.emails.EmailServerWrapper.Credentials
import resumes.emails.MessageParser.Message
import resumes.response.ResponseManager.Response
import resumes.response.relevance.identifiers.IBMRelevanceIdentifier

import scala.util.{Failure, Success}

object ResponseManager {

  val DECLINED = "DECLINED"
  val ACCEPTED = "ACCEPTED"
  val UNKNOWN = "UNKNOWN"
  val EMAIL_BLOCKED = "EMAIL_BLOCKED"

  case class Response(
                       messages: List[Message] = List(),
                       lastTimeChecked: Date = new Date(),
                       decision: String = UNKNOWN
                     )

}

class ResponseManager(applicationManager: ApplicationManager,
                      emailServerWrapper: EmailServerWrapper
                     ) {

  private val companyToIdentifier = Map(
    Companies.IBM.toString -> IBMRelevanceIdentifier
  )

  private val logger = LogManager.getLogger(this.getClass)

  private val emailsManager = applicationManager.getEmailsManager

  def collectResponses() = {
    val applications = applicationManager.getAllApplicationsWithUnknownResponse()
    applications.foreach(application => {
      val emailPassword = emailsManager.getPassword(application.email)
      val credentials = Credentials(application.email, emailPassword)
      emailServerWrapper.getAllMessages(credentials) match {
        case Success(retrievedMessages) => {
          emailsManager.accessedSuccessfully(application.email)
          val existingMessages = application.response match {
            case None => List.empty[Message]
            case Some(response) => response.messages
          }
          val filteredMessages = retrievedMessages.filter(message => {
            companyToIdentifier.get(application.company).get.isRelevant(application, message)
          })
          val resultMessages = mergeMessagesLists(existingMessages, filteredMessages)
          val newResponse = application.response match {
            case None => {
              Response(messages = resultMessages)
            }
            case Some(response) => {
              response.copy(messages = resultMessages).copy(lastTimeChecked = new Date())
            }
          }
          applicationManager.updateResponse(application.id, newResponse)
        }
        case Failure(e) => {
          logger.error(s"Could not get messages from email: ${application.email} for application ${application.id}", e)
          emailsManager.failedToAccess(application.email)
        }
      }
    })
  }

  private def mergeMessagesLists(firstList: List[Message], secondList: List[Message]) = {
    (firstList.toSet ++ secondList.toSet).toList
  }


}
