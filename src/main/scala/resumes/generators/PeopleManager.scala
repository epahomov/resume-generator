package resumes.generators

import com.mongodb.client.MongoDatabase
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

  def getRandomPerson(): Option[Person] = {
    val peopleCollection = database.getCollection(PEOPLE_COLLECTION)
    peopleCollection.find().first() match {
      case null => None
      case doc => Some(parse(doc.toJson).extract[Person])
    }
  }

}
