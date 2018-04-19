package resumes.generators.person

import java.util.UUID

import resumes.company.PositionManager.{Area, ExperienceLevel, Position}
import resumes.generators.Utils
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
    val area = position.area.map(Area.withName(_))
    val internshipExperience = InternshipGenerator.generateInternships(education, area)
    if (!experienceLevel.equals(ExperienceLevel.Freshly_Graduate)) {
      val (realWorkExperience, workExperienceComment) = EmploymentGenerator.generateEmployment(area.get, graduationYear, position.previousPosition, position.skills)
      val (address, addressComment) = AddressGenerator.generateAddress(education, true, position)
      val phoneNumber = PhoneNumberGenerator.generateRandomNumber(address.stateShortName)
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
      val phoneNumber = PhoneNumberGenerator.generateRandomNumber(address.stateShortName)
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


  private lazy val genderGenerator = {
    val distribution = List(
      (Gender.Female, 1),
      (Gender.Male, 3)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  private lazy val originGenerator = {
    val distribution = List(
      (Origin.US, 10),
      (Origin.China, 7),
      (Origin.Arab, 2),
      (Origin.India, 13)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  def generatePerson(position: Position): Person = {
    val gender = genderGenerator.sample()
    val origin = originGenerator.sample()
    generatePerson(gender, origin, position)
  }


  def generateRandomPeople(num: Int, position: Position): List[Person] = {
    (1 to num).map(_ => generatePerson(position)).toList
  }

}
