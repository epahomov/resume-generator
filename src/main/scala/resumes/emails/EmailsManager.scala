package resumes.emails

import java.util.Date

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates._
import net.liftweb.json.parse
import org.bson.Document
import org.joda.time.{DateTime, Days}
import resumes.MongoDB
import resumes.emails.EmailsManagerUtils.{Email, formats}
import resumes.Utils._

import scala.collection.JavaConverters._
import scala.util.Random

class EmailsManager(database: MongoDatabase) {

  private lazy val emails = database.getCollection(EmailsManagerUtils.EMAILS_COLLECTION_NAME)

  private val companiesInWhichBeenUsed = "companiesInWhichBeenUsed"

  private val MAX_EMAIL_CHECKED_ATTEMPTS = 3

  def markEmailAsUsedForApplication(email: String, company: String): Long = {
    val filter = Filters.eq("address", email)
    val update = addToSet(companiesInWhichBeenUsed, company)
    emails.updateOne(filter, update).getModifiedCount
  }

  def getAllEmails(): List[Email] = {
    emails.find().asScala.map(doc => {
      parse(doc.toJson).extract[Email]
    }).toList
  }

  def uploadEmail(email: Email) = {
    MongoDB.insertValueIntoCollection(email, emails)
  }

  def getPassword(address: String): String = {
    emails.find(Filters.eq("address", address)).first().getString("password")
  }

  def getEmail(address: String): Email = {
    fromDoc(emails.find(Filters.eq("address", address)).first())
  }

  def accessedSuccessfully(address: String): Unit = {
    val filter = Filters.eq("address", address)
    val email: Email = fromDoc(emails.find(filter).first())
    val newEmail = email.copy(numberOfFails = Some(0), lastTimeChecked = Some(today().toDate))
    emails.replaceOne(filter, toDoc(newEmail))
  }

  def failedToAccess(address: String) = {
    val filter = Filters.eq("address", address)
    val email: Email = fromDoc(emails.find(filter).first())
    val someTimeAgo = new Date(System.currentTimeMillis() - 7L * 24 * 3600 * 1000)
    val oldLastTimeChecked = new DateTime(email.lastTimeChecked.getOrElse(someTimeAgo))

    val newNumberOfFails = if (Days.daysBetween(oldLastTimeChecked, today).getDays > 0) {
      email.numberOfFails match {
        case Some(currentNumber) => currentNumber + 1
        case None => 1
      }
    } else {
      email.numberOfFails.getOrElse(0)
    }
    val newActive = if (newNumberOfFails > MAX_EMAIL_CHECKED_ATTEMPTS) {
      Some(false)
    } else {
      email.active
    }
    val newLastTimeChecked = today.toDate
    val newEmail = email
      .copy(numberOfFails = Some(newNumberOfFails))
      .copy(active = newActive)
      .copy(lastTimeChecked = Some(newLastTimeChecked))
    emails.replaceOne(filter, toDoc(newEmail))
  }

  protected def today() = new DateTime()

  def getNotUsedEmail(company: String): Option[String] = {
    val filter = Filters.and(Filters.not(Filters.in(companiesInWhichBeenUsed, company)),
      Filters.or(
        Filters.not(Filters.exists("active")),
        Filters.eq("active", true)
      ))
    val notUsedEmails = emails
      .find(filter)
      .asScala
      .toList

    def getAddress(doc: Document) = {
      doc.get("address").toString
    }

    if (notUsedEmails.size > 1) {
      val doc = notUsedEmails(Random.nextInt(notUsedEmails.size - 1))
      Some(getAddress(doc))
    } else {
      notUsedEmails.headOption.map(getAddress)
    }
  }

  private def fromDoc(document: Document): Email = {
    parse(document.toJson()).extract[Email]
  }

}
