package resumes.generators

import java.util.UUID

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

import scala.io.Source
import scala.util.Random

object PersonGenerator {

  implicit val formats = net.liftweb.json.DefaultFormats + new EnumSerializer(Gender)  + new EnumSerializer(Origin)

  case class Person(name: Name,
                    education: List[Education],
                    address: Address,
                    phoneNumber: String,
                    gender: Gender,
                    origin: Origin
                   )

  case class Candidate(id: String, person: Person, email: String, passd: String)


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

  def serializeCandidate(candidate: Candidate): String = {
    prettyRender(decompose(candidate))
  }

  def deserializeCandidate(json: String): Candidate = {
    parse(json).extract[Candidate]
  }

  def generateRandomPeople() = {
    var count = 0
    Source
      .fromResource("emails_test.txt")
        .getLines().foreach(credentials => {
      count += 1
      println(count)
      val email = credentials.split(":")(0)
      val passwd = credentials.split(":")(1)
      val id = UUID.randomUUID().toString
      val path = s"/Users/macbook/IdeaProjects/gmailaccountcreeator/src/main/resources/applications/ibm/candidates/$id.json"
      val person = generatePerson()
      val candidate = Candidate(id, person, email, passwd)
      val personJson = serializeCandidate(candidate)
      //println(personJson)
      val personBack = deserializeCandidate(personJson)
      scala.tools.nsc.io.File(path).writeAll(personJson)
    }
    )
  }

  def main(args: Array[String]): Unit = {
    generateRandomPeople()
  }
}
