package resumes.emails

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates._
import net.liftweb.json.parse
import resumes.emails.EmailsManagerUtils.Email

import scala.collection.JavaConverters._
import EmailsManagerUtils.formats

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

  def getNotUsedEmail(company: String): Option[String] = {
    val iterator = emails.find(Filters.not(Filters.in(companiesInWhichBeenUsed, company)))
    iterator.first() match {
      case null => None
      case doc => Some(doc.get("address").toString)
    }
  }

}
