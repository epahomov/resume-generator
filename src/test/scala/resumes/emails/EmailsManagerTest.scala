package resumes.emails

import org.junit.{Before, Test}
import resumes.MongoTest

class EmailsManagerTest extends MongoTest{


  var emailsManager: EmailsManager = null

  val company = "ibm"
  val otherCompany = "google"

  @Before
  def setUpEmailsCollection = {
    EmailsManagerUtils.uploadEmails("emails_test.txt", _mongo_database)
    emailsManager = new EmailsManager(_mongo_database)
  }

  @Test
  def testEmailsManager(): Unit = {

    (0 to 3).foreach(_ => {
      val email1 = emailsManager.getNotUsedEmail(company).get
      emailsManager.markEmailAsUsed(email1, company)
    })
    assert(emailsManager.getNotUsedEmail(company) === None)
    (0 to 3).foreach(_ => {
      val email1 = emailsManager.getNotUsedEmail(otherCompany).get
      emailsManager.markEmailAsUsed(email1, otherCompany)
    })
    assert(emailsManager.getNotUsedEmail(otherCompany) === None)
  }

}
