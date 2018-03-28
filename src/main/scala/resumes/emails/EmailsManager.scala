package resumes.emails

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates._
import net.liftweb.json.parse
import resumes.emails.EmailsManagerUtils.Email

import scala.collection.JavaConverters._
import EmailsManagerUtils.formats
import org.bson.Document

import scala.util.Random

class EmailsManager(database: MongoDatabase) {

  lazy val emails = database.getCollection(EmailsManagerUtils.EMAILS_COLLECTION_NAME)

  private val companiesInWhichBeenUsed = "companiesInWhichBeenUsed"

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

  def getNotUsedEmail(company: String): Option[String] = {
    val notUsedEmails = emails
      .find(Filters.not(Filters.in(companiesInWhichBeenUsed, company)))
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
