package resumes.response

import java.util.Date

import org.apache.logging.log4j.LogManager
import resumes.applications.ApplicationManager
import resumes.company.CompanyManager.Companies
import resumes.emails.EmailServerWrapper
import resumes.emails.EmailServerWrapper.Credentials
import resumes.emails.MessageParser.Message
import resumes.response.ResponseCollector.Response
import resumes.response.relevance.identifiers.{IBMRelevanceIdentifier, SalesForceRelevanceIdentifier}

import scala.collection.parallel.ForkJoinTaskSupport
import scala.util.{Failure, Success}

object ResponseCollector {

  object Decision extends Enumeration {
    type Decision = Value
    val DECLINED = Value("DECLINED")
    val ACCEPTED = Value("ACCEPTED")
    val UNKNOWN = Value("UNKNOWN")
  }

  case class Response(
                       messages: List[Message] = List(),
                       lastTimeChecked: Date = new Date(),
                       decision: String = Decision.UNKNOWN.toString
                     )

}

class ResponseCollector(applicationManager: ApplicationManager,
                        emailServerWrapper: EmailServerWrapper
                     ) {

  private val companyToIdentifier = Map(
    Companies.IBM.toString -> IBMRelevanceIdentifier,
    Companies.SalesForce.toString -> SalesForceRelevanceIdentifier
  )

  private val logger = LogManager.getLogger(this.getClass)

  private val emailsManager = applicationManager.getEmailsManager

  def collectResponses() = {
    val applications = applicationManager.getAllApplicationsWithUnknownResponse().par
    applications.tasksupport =  new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(10))
    applications.map(application => {
      logger.info(s"Working with application ${application.id}")
      val emailPassword = emailsManager.getPassword(application.email)
      val credentials = Credentials(application.email, emailPassword)
      emailServerWrapper.getAllMessages(credentials) match {
        case Success(retrievedMessages) => {
          logger.info(s"Successfully retrieved messages for ${application.email}." +
            s" Number of retrieved messages ${retrievedMessages.size}")
          emailsManager.accessedSuccessfully(application.email)
          val existingMessages = application.response match {
            case None => List.empty[Message]
            case Some(response) => response.messages
          }
          val filteredMessages = retrievedMessages.filter(message => {
            companyToIdentifier.get(application.company).get.isRelevant(application, message)
          })
          logger.info(s"${application.id} :Number of messages, which passed filter is ${filteredMessages.size}")
          filteredMessages.foreach(message => {
            logger.info(message)
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
    }).toList
  }

  private def mergeMessagesLists(firstList: List[Message], secondList: List[Message]) = {
    (firstList.toSet ++ secondList.toSet).toList
  }


}
