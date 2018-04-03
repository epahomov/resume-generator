package resumes.applications

import org.junit.{Before, Test}
import resumes.MongoTest
import resumes.company.PositionManager
import resumes.emails.{EmailsManager, EmailsManagerUtils}
import resumes.response.ResponseManager._

import scala.util.Random

class ApplicationManagerTest extends MongoTest {

  var applicationManager: ApplicationManager = null
  val applicationCompany = "ibm"
  var updatedEmailManager = false
  var updatedPositionManager = false

  @Before
  def setUpEmailsCollection = {

    EmailsManagerUtils.uploadEmails("emails_test.txt", _mongo_database)
    val emailsManager = new EmailsManager(_mongo_database) {
      override def markEmailAsUsedForApplication(email: String, company: String): Long = {
        updatedEmailManager = true
        assert(company === applicationCompany)
        0L
      }
    }
    val positionsManager = new PositionManager(_mongo_database) {
      override def successfullyAppliedForPosition(id: String) = {
        updatedPositionManager = true
      }
    }
    applicationManager = new ApplicationManager(emailsManager,
      positionsManager,
      _mongo_database
    )
  }

  @Test
  def testGetAllApplicationsWithUnknownResponse(): Unit = {
    val dummyApplication = DummyApplication.veryPlainApplication("ibm", "123")
    applicationManager.storeApplication(dummyApplication)
    assert(applicationManager.getAllApplicationsWithUnknownResponse().size === 1)
    val unknownResponse =
      dummyApplication
        .copy(id = Random.nextString(10))
        .copy(response = Some(Response(decision = UNKNOWN)))
    applicationManager.storeApplication(unknownResponse)
    assert(applicationManager.getAllApplicationsWithUnknownResponse().size === 2)
    val knownResponse =
      dummyApplication
        .copy(id = Random.nextString(10))
        .copy(response = Some(Response(decision = ACCEPTED)))
    applicationManager.storeApplication(knownResponse)
    assert(applicationManager.getAllApplicationsWithUnknownResponse().size === 2)
  }


  @Test
  def testUpdateResponse(): Unit = {
    val dummyApplication = DummyApplication.veryPlainApplication(applicationCompany, "123")
    applicationManager.storeApplication(dummyApplication)
    assert(applicationManager.getApplication(dummyApplication.id).response === None)
    val response = Response(decision = ACCEPTED)
    applicationManager.updateResponse(dummyApplication.id, response)
    assert(applicationManager.getApplication(dummyApplication.id).response.get.decision === ACCEPTED)
  }

  @Test
  def testUpdateAllComponents(): Unit = {
    val dummyApplication = DummyApplication.veryPlainApplication(applicationCompany, "123")
    applicationManager.updateAllComponents(dummyApplication)
    assert(updatedEmailManager === true)
    assert(updatedPositionManager === true)
  }
}
