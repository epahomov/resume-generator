package resumes.generators.person

import java.util.UUID

import resumes.generators.PeopleManager.Person
import resumes.generators.education.EducationGenerator
import resumes.generators.name.FirstNameGenerator.Gender.Gender
import resumes.generators.name.FirstNameGenerator.Origin.Origin
import resumes.generators.name.FirstNameGenerator.{Gender, Origin}
import resumes.generators.name.NameGenerator

import scala.util.Random

object PersonGenerator {

  def generatePerson(gender: Gender, origin: Origin) = {
    val name = NameGenerator.generateRandomName(gender, origin)
    val education = EducationGenerator.generateEducation()
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


  def generateRandomPeople(num: Int): List[Person] = {
    
    (1 to num).map(_ => generatePerson()).toList
  }

}
