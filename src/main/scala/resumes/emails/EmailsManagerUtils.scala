package resumes.emails

import java.util.Date

import com.mongodb.client.{MongoCollection, MongoDatabase}
import net.liftweb.json.Extraction.decompose
import net.liftweb.json.JsonAST.prettyRender
import org.bson.Document
import resumes.MongoDB

import scala.collection.JavaConverters._
import scala.io.Source
object EmailsManagerUtils {

  case class Email(
                    address: String,
                    password: String,
                    companiesInWhichBeenUsed: List[String],
                    lastTimeChecked: Option[Date] = None,
                    numberOfFails: Option[Int] = None,
                    active: Option[Boolean] = None
                  )

  implicit val formats = net.liftweb.json.DefaultFormats

  val EMAILS_COLLECTION_NAME = "emails"

  def uploadEmails(path: String, mongoDatabase: MongoDatabase): Unit = {
    MongoDB.createCollectionIfNotExists(EMAILS_COLLECTION_NAME, mongoDatabase)
    val emails = mongoDatabase.getCollection(EMAILS_COLLECTION_NAME)
    uploadEmails(path, emails)
  }

  private def uploadEmails(path: String, emails: MongoCollection[Document]): Unit = {
    val emailsParsed = Source
      .fromResource(path)
      .getLines()
      .map(line => {
        val address = line.split(":")(0)
        val password = line.split(":")(1)
        val email = Email(address = address,
          password = password,
          companiesInWhichBeenUsed = List.empty
        )
        Document.parse(prettyRender(decompose(email)))
      }
      ).toList.asJava
    emails.insertMany(emailsParsed)
  }

}
