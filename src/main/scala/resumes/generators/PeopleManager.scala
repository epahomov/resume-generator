package resumes.generators

import com.mongodb.client.{FindIterable, MongoDatabase}
import resumes.MongoDB
import resumes.generators.education.EducationGenerator.Education
import resumes.generators.name.FirstNameGenerator.Gender.Gender
import resumes.generators.name.FirstNameGenerator.Origin.Origin
import resumes.generators.name.NameGenerator.Name
import resumes.generators.person.AddressGenerator.Address
import resumes.generators.person.PersonGenerator.generateRandomPeople
import resumes.generators.PeopleManager.{PEOPLE_COLLECTION, Person}
import net.liftweb.json.parse
import MongoDB.formats
import com.mongodb.client.model.Filters
import org.bson.Document


object PeopleManager {

  val PEOPLE_COLLECTION = "people"

  case class Person(id: String,
                    name: Name,
                    education: List[Education],
                    address: Address,
                    phoneNumber: String,
                    gender: Gender,
                    origin: Origin
                   )

  def main(args: Array[String]): Unit = {
    val peopleManager = new PeopleManager(MongoDB.database)
    peopleManager.storePeople(1000)
  }

}

class PeopleManager(database: MongoDatabase) {

  def storePeople(num: Int) = {
    MongoDB.createCollectionIfNotExists(PEOPLE_COLLECTION, database)
    val peopleCollection = database.getCollection(PEOPLE_COLLECTION)
    val people = generateRandomPeople(num)
    MongoDB.insertIntoCollection(people, peopleCollection)
  }

  def deletePerson(person: Person) = {
    database.getCollection(PEOPLE_COLLECTION).deleteOne(Filters.eq("id", person.id))
  }

  def getPersonById(id: String): Option[Person] = {
    val docs = database.getCollection(PEOPLE_COLLECTION).find(Filters.eq("id", id))
    getPerson(docs)
  }

  def getRandomPerson(): Option[Person] = {
    val docs = database.getCollection(PEOPLE_COLLECTION).find()
    getPerson(docs)
  }

  private def getPerson(docs: FindIterable[Document]) = {
    docs.first() match {
      case null => None
      case doc => Some(parse(doc.toJson).extract[Person])
    }
  }
}
