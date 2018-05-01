package resumes.generators.linkedin

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import net.liftweb.json.parse
import org.bson.Document
import resumes.MongoDB
import resumes.generators.linkedin.LinkedInParser.LinkedInPerson
import scala.collection.JavaConverters._

class LinkedInPersonManager(database: MongoDatabase) {

  private lazy val people = {
    MongoDB.createCollectionIfNotExists(LinkedInPerson_COLLECTION_NAME, database)
    database.getCollection(LinkedInPerson_COLLECTION_NAME)
  }

  implicit val formats = net.liftweb.json.DefaultFormats

  val LinkedInPerson_COLLECTION_NAME = "linkedin"


  def uploadPerson(person: LinkedInPerson) = {
    MongoDB.insertValueIntoCollection(person, people)
  }

  def getUrls() = {
    people.find().asScala.map(fromDoc).map(_.url).toSet
  }

  def getPerson(url: String): LinkedInPerson = {
    fromDoc(people.find(Filters.eq("url", url)).first())
  }

  def isPersonExist(url: String): Boolean = {
    people.find(Filters.eq("url", url)).iterator().hasNext
  }

  private def fromDoc(document: Document): LinkedInPerson = {
    parse(document.toJson()).extract[LinkedInPerson]
  }

}
