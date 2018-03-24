package resumes.emails

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates._


class EmailsManager(database: MongoDatabase) {

  implicit val formats = net.liftweb.json.DefaultFormats

  val emails = database.getCollection("emails")

  private val companiesInWhichBeenUsed = "companiesInWhichBeenUsed"

  def markEmailAsUsed(email: String, company: String): Unit = {
    val filter = Filters.eq("address", email)
    val update = addToSet(companiesInWhichBeenUsed, company)
    emails.updateOne(filter, update)
  }

  def getNotUsedEmail(company: String): Option[String] = {
    val iterator = emails.find(Filters.elemMatch(companiesInWhichBeenUsed, Filters.eq(company)))
    iterator.first() match {
      case null => None
      case doc => Some(doc.get("address").toString)
    }
  }

}
