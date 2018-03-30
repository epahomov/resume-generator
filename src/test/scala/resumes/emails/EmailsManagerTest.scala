package resumes.emails

import org.joda.time.DateTime
import org.junit.{Before, Test}
import resumes.MongoTest
import resumes.emails.EmailsManagerUtils.Email

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
  def testMarkAsUsed(): Unit = {

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

  @Test
  def testUsedSuccessfully(): Unit = {
    val address = "3425w345"
    val email = Email(address, "sdfasdas", List.empty, numberOfFails = Some(2))
    emailsManager.uploadEmail(email)
    assert(emailsManager.getEmail(address).numberOfFails.get === 2)
    emailsManager.usedSuccessfully(address)
    assert(emailsManager.getEmail(address).numberOfFails.getOrElse(0) === 0)
  }

  @Test
  def testFailedToUse(): Unit = {
    val address = "loletaglasser@yahoo.com"
    def email() = {
      emailsManager.getEmail(address)
    }

    assert(email.numberOfFails.getOrElse(0) === 0 )
    assert(email.active.getOrElse(true) === true )

    emailsManager.failedToUse(address)

    assert(email.numberOfFails.get === 1 )
    assert(email.active.getOrElse(true) === true )

    emailsManager.failedToUse(address)

    assert(email.numberOfFails.get === 1 )
    assert(email.active.getOrElse(true) === true )

    var newEmailsManager = new EmailsManager(_mongo_database) {
      override def today() = (new DateTime()).plusDays(2)
    }

    newEmailsManager.failedToUse(address)

    assert(email.numberOfFails.get === 2 )
    assert(email.active.getOrElse(true) === true )

    newEmailsManager.failedToUse(address)
    newEmailsManager.failedToUse(address)
    newEmailsManager.failedToUse(address)

    assert(email.numberOfFails.get === 2 )
    assert(email.active.getOrElse(true) === true )

    newEmailsManager = new EmailsManager(_mongo_database) {
      override def today() = (new DateTime()).plusDays(4)
    }

    newEmailsManager.failedToUse(address)

    assert(email.numberOfFails.get === 3 )
    assert(email.active.getOrElse(true) === true )

    newEmailsManager = new EmailsManager(_mongo_database) {
      override protected def today() = (new DateTime()).plusDays(6)
    }

    newEmailsManager.failedToUse(address)

    assert(email.numberOfFails.get === 4 )
    assert(email.active.get === false )

  }

}
