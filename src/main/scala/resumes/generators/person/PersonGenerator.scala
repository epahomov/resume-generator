package resumes.generators.person

import java.util.UUID

import resumes.company.PositionManager.Position
import resumes.generators.education.EducationGenerator
import resumes.generators.education.EducationGenerator.Education
import resumes.generators.name.FirstNameGenerator.Gender.Gender
import resumes.generators.name.FirstNameGenerator.Origin.Origin
import resumes.generators.name.FirstNameGenerator.{Gender, Origin}
import resumes.generators.name.NameGenerator
import resumes.generators.name.NameGenerator.Name
import resumes.generators.person.AddressGenerator.Address

import scala.util.Random

object PersonGenerator {

  case class Person(id: String,
                    name: Name,
                    education: List[Education],
                    address: Address,
                    phoneNumber: String,
                    gender: Gender,
                    origin: Origin
                   )

  def generatePerson(gender: Gender, origin: Origin, position: Position) = {
    val name = NameGenerator.generateRandomName(gender, origin)
    val education = EducationGenerator.generateEducation(position)
    val address = AddressGenerator.generateAddress(education(0).university.city + ", " + education(0).university.state)
    val phoneNumber = PhoneNumberGenerator.generateRandomNumber()
    Person(
      id = UUID.randomUUID().toString,
      name = name,
      education = education,
      address = address,
      phoneNumber = phoneNumber,
      gender = gender,
      origin = origin
    )
  }

  def generatePerson(position: Position): Person = {
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
    generatePerson(gender, origin, position)
  }


  def generateRandomPeople(num: Int, position: Position): List[Person] = {
    (1 to num).map(_ => generatePerson(position)).toList
  }

}
