package resumes.generators

import resumes.generators.AddressGenerator.Address
import resumes.generators.education.EducationGenerator
import resumes.generators.education.EducationGenerator.{Degree, Education}
import resumes.generators.name.FirstNameGenerator.{Gender, Origin}
import resumes.generators.name.FirstNameGenerator.Gender.Gender
import resumes.generators.name.NameGenerator
import resumes.generators.name.NameGenerator.Name
import net.liftweb.json.JsonAST._
import net.liftweb.json.Extraction._
import net.liftweb.json.ext.EnumSerializer
import net.liftweb.json.parse
import resumes.generators.name.FirstNameGenerator.Origin.Origin

import scala.util.Random

object PersonGenerator {

  implicit val formats = net.liftweb.json.DefaultFormats + new EnumSerializer(Gender) + new EnumSerializer(Degree) + new EnumSerializer(Origin)

  case class Person(name: Name,
                    education: List[Education],
                    address: Address,
                    phoneNumber: String,
                    gender: Gender,
                    origin: Origin
                   )

  def generatePerson(gender: Gender, origin: Origin) = {
    val name = NameGenerator.generateRandomName(gender, origin)
    val education = EducationGenerator.generateEducation()
    val address = AddressGenerator.generateAddress(education(0).university.city + ", " + education(0).university.state)
    val phoneNumber = PhoneNumberGenerator.generateRandomNumber()
    Person(
      name = name,
      education = education,
      address = address,
      phoneNumber = phoneNumber,
      gender = gender,
      origin = origin
    )
  }

  def generatePerson(): Person = {
    val gender = if (Random.nextBoolean()) {
      Gender.Female
    } else {
      Gender.Male
    }
    val origin = if (Random.nextBoolean()) {
      Origin.India
    } else {
      Origin.US
    }
    generatePerson(gender, origin)
  }

  def serializePerson(person: Person): String = {
    prettyRender(decompose(person))
  }

  def deserializePerson(json: String): Person = {
    parse(json).extract[Person]
  }

  def generateRandomPeople() = {
    (39 to 1000).foreach(index => {
      println(index)
      val path = s"/Users/macbook/IdeaProjects/gmailaccountcreeator/src/main/resources/random_resume/$index.json"
      val person = generatePerson()
      val personJson = serializePerson(person)
      scala.tools.nsc.io.File(path).writeAll(personJson)
    }
    )
  }

  def main(args: Array[String]): Unit = {
    generateRandomPeople()
  }
}
