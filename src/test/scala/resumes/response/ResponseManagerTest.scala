package resumes.response

import java.util.Date

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import resumes.applications.ApplicationManager.Application
import resumes.applications.{ApplicationManager, DummyApplication}
import resumes.emails.EmailServerWrapper.Credentials
import resumes.emails.MessageParser.Message
import resumes.emails.{EmailServerWrapper, EmailsManager, MessageParser}
import resumes.response.ResponseManager.{Response, _}

import scala.util.{Random, Success, Try}

class ResponseManagerTest extends JUnitSuite {

  @Test
  def testCollectResponses(): Unit = {

    val message1 = Message(
      senders = List("somesender@ibm.com"),
      recipients = List("me@yahoo.com"),
      subject = "We have offer for you",
      date = new Date(),
      text = "sdfsdfsdf",
      id = "1"
    )

    val message1Again = Message(
      senders = List("somesender@ibm.com"),
      recipients = List("me@yahoo.com"),
      subject = "We have offer for you",
      date = new Date(),
      text = "sdfsdfsdf",
      id = "1"
    )

    val message2 = Message(
      senders = List("someOthersender@ibm.com"),
      recipients = List("me@yahoo.com"),
      subject = "We reject you",
      date = new Date(),
      text = "egsegewrgerwtew",
      id = "2"
    )

    val message3 = Message(
      senders = List("googleguy@google.com"),
      recipients = List("me@yahoo.com"),
      subject = "We reject you",
      date = new Date(),
      text = "hjbjhbhb",
      id = "3"
    )

    val dummyApplication = DummyApplication
      .veryPlainApplication("ibm", "1234")
      .copy(response = Some(Response(messages = List(message1), decision = ACCEPTED)))
    var methodsInvocation = 0
    val emailPasswd = Random.nextString(10)

    val emailsManager = new EmailsManager(null) {

      override def accessedSuccessfully(address: String): Unit = {
        methodsInvocation += 1
        assert(address === dummyApplication.email)
      }

      override def getPassword(address: String): String = {
        methodsInvocation += 1
        emailPasswd
      }

    }

    val applicationManager = new ApplicationManager(emailsManager, null, null, null) {

      override def getAllApplicationsWithUnknownResponse(): List[Application] = {
        methodsInvocation += 1
        List(dummyApplication)
      }

      override def updateResponse(applicationId: String, response: Response): Unit = {
        methodsInvocation += 1
        assert(applicationId === dummyApplication.id)
        assert(response.messages.size === 2)
      }
    }

    val emailServerWrapper = new EmailServerWrapper {

      override def getAllMessages(credentials: Credentials, tryAttempts: Int = 3): Try[List[MessageParser.Message]] = {
        methodsInvocation += 1
        assert(dummyApplication.email === credentials.login)
        assert(emailPasswd === credentials.psswd)
        Success(List(message1Again, message2, message3))
      }
    }

    val responseManager = new ResponseManager(applicationManager,
      emailServerWrapper
    )

    responseManager.collectResponses()
    assert(methodsInvocation === 5)



  }


}
