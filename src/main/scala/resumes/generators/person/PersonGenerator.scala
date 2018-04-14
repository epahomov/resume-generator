package resumes.generators.person

import java.util.UUID

import resumes.company.PositionManager.{Area, ExperienceLevel, Position}
import resumes.generators.education.EducationGenerator
import resumes.generators.education.EducationGenerator.Education
import resumes.generators.name.FirstNameGenerator.Gender.Gender
import resumes.generators.name.FirstNameGenerator.Origin.Origin
import resumes.generators.name.FirstNameGenerator.{Gender, Origin}
import resumes.generators.name.NameGenerator
import resumes.generators.name.NameGenerator.Name
import resumes.generators.person.AddressGenerator.Address
import resumes.generators.work.EmploymentGenerator.Employment
import resumes.generators.work.{EmploymentGenerator, InternshipGenerator}

import scala.util.Random

object PersonGenerator {

  case class Person(id: String,
                    name: Name,
                    education: List[Education],
                    address: Address,
                    phoneNumber: String,
                    gender: String,
                    origin: String,
                    workExperience: List[Employment],
                    comments: Option[List[String]] = None
                   )

  type Comment = String

  private def generatePerson(gender: Gender, origin: Origin, position: Position) = {
    val (experienceLevel, experienceLevelComment) = ExperienceLevelGenerator.generateExperienceLevel(position)
    val name = NameGenerator.generateRandomName(gender, origin)
    val graduationYear = EmploymentGenerator.getGraduationYear(experienceLevel)
    val (education, educationComment) = EducationGenerator.generateEducation(position, graduationYear)
    val phoneNumber = PhoneNumberGenerator.generateRandomNumber()
    val area = position.area.map(Area.withName(_))
    val internshipExperience = InternshipGenerator.generateInternships(education, area)
    if (!experienceLevel.equals(ExperienceLevel.Freshly_Graduate)) {
      val (realWorkExperience, workExperienceComment) = EmploymentGenerator.generateEmployment(area.get, graduationYear, position.previousPosition, position.skills)
      val (address, addressComment) = AddressGenerator.generateAddress(education, true, position)
      Person(
        id = UUID.randomUUID().toString,
        name = name,
        education = education,
        address = address,
        phoneNumber = phoneNumber,
        gender = gender.toString,
        origin = origin.toString,
        workExperience = internshipExperience ++ realWorkExperience,
        comments = Some(List(experienceLevelComment, educationComment, addressComment, workExperienceComment))
      )
    } else {
      val (address, addressComment) = AddressGenerator.generateAddress(education, false, position)
      Person(
        id = UUID.randomUUID().toString,
        name = name,
        education = education,
        address = address,
        phoneNumber = phoneNumber,
        gender = gender.toString,
        origin = origin.toString,
        workExperience = internshipExperience,
        comments = Some(List(experienceLevelComment, educationComment, addressComment))
      )
    }

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
