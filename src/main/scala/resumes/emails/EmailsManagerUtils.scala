package resumes.emails

import com.mongodb.client.MongoCollection
import net.liftweb.json.Extraction.decompose
import net.liftweb.json.JsonAST.prettyRender
import org.bson.Document

import scala.collection.JavaConverters._
import scala.io.Source

object EmailsManagerUtils {

  case class Email(
                    address: String,
                    password: String,
                    companiesInWhichBeenUsed: List[String],
                  )

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
        implicit val formats = net.liftweb.json.DefaultFormats
        Document.parse(prettyRender(decompose(email)))
      }
      ).toList.take(100).asJava
    emails.insertMany(emailsParsed)
  }

}
