package resumes.emails

import java.util.Date

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates._
import net.liftweb.json.parse
import resumes.emails.EmailsManagerUtils.Email

import scala.collection.JavaConverters._
import EmailsManagerUtils.formats
import org.bson.Document
import org.joda.time.{DateTime, Days}
import resumes.Utils._

import scala.util.Random

class EmailsManager(database: MongoDatabase) {

  lazy val emails = database.getCollection(EmailsManagerUtils.EMAILS_COLLECTION_NAME)

  private val companiesInWhichBeenUsed = "companiesInWhichBeenUsed"

  val MAX_EMAIL_CHECKED_ATTEMPTS = 3

  def markEmailAsUsed(email: String, company: String): Long = {
    val filter = Filters.eq("address", email)
    val update = addToSet(companiesInWhichBeenUsed, company)
    emails.updateOne(filter, update).getModifiedCount
  }

  def getAllEmails(): List[Email] = {
    emails.find().asScala.map(doc => {
      parse(doc.toJson).extract[Email]
    }).toList
  }

  def getPassword(address: String): String = {
    emails.find(Filters.eq("address", address)).first().getString("password")
  }

  def failedToUse(address: String) = {
    val filter = Filters.eq("address", address)
    val email: Email = fromDoc(emails.find(filter).first())
    val lastTimeChecked = new DateTime(email.lastTimeChecked.getOrElse(new Date))
    val amendedLastTimeCheckedEmail = if (Days.daysBetween(lastTimeChecked, new DateTime()).getDays > 0) {
      email.copy(numberOfFails = {
        Some(email.numberOfFails match {
          case Some(currentNumber) => currentNumber + 1
          case None => 0
        })
      })
    } else {
      email
    }
    val newEmail = if (amendedLastTimeCheckedEmail.numberOfFails.getOrElse(0) > MAX_EMAIL_CHECKED_ATTEMPTS) {
      amendedLastTimeCheckedEmail.copy(active = Some(false))
    }
    //emails.replaceOne()
  }

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

}
