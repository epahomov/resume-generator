package resumes.applications

import java.util.Date

import resumes.applications.ApplicationManager.Application
import resumes.generators.education.EducationGenerator.Education
import resumes.generators.education.Enums.Degree
import resumes.generators.education.UniversityGenerator.University
import resumes.generators.name.FirstNameGenerator.{Gender, Origin}
import resumes.generators.name.NameGenerator.Name
import resumes.generators.person.AddressGenerator.Address
import resumes.generators.person.PersonGenerator.Person
import resumes.generators.person.PhoneNumberGenerator
import resumes.generators.work.EmploymentGenerator.Employment

import scala.util.Random

object DummyApplication {


  def veryPlainApplication(company: String, positionUrl: String) = {
    val associate = Education(startYear = 2012, endYear = 2014, university = University("Stanford", "Palo Alto", "CA"), degree = Degree.Associate.toString)
    val bachelor = Education(startYear = 2014, endYear = 2016, university = University("Stanford", "Palo Alto", "CA"), degree = Degree.Bachelor.toString)
    val masters = Education(startYear = 2016, endYear = 2018, university = University("Stanford", "Palo Alto", "CA"), degree = Degree.Master.toString)
    val education = List(masters, bachelor, associate)
    val workExperience = List(Employment(
      start = new Date(2013 - 1900, 5, 1),
      end = new Date(2013 - 1900, 5, 1),
      company = "Amazon",
      description = "",
      role = "Intern",
      skillsFormatted = Some("javascript python")
    ),
      Employment(
        start = new Date(2014 - 1900, 5, 1),
        end = new Date(2014 - 1900, 5, 1),
        company = "Apple",
        description = "",
        role = "Intern",
        skillsFormatted = Some("java python")
      ))
    val address = Address(zipCode = "94402",
      stateFullName = "California",
      stateShortName = "CA",
      city = "Palo Alto",
      street = "Not your business drive",
      house = "1234"
    )

    val person = Person(
      id = "1234",
      name = Name(
        firstName = "TestingFirstName",
        lastName = "TestingLastName"
      ),
      education = education,
      address = address,
      phoneNumber = PhoneNumberGenerator.generateRandomNumber(),
      gender = Gender.Male.toString,
      origin = Origin.US.toString,
      workExperience
    )
    Application(person = person,
      company = company,
      positionUrl = positionUrl,
      positionId = "1234",
      email = Random.nextInt(10000) + "blabla@gmail.com"
    )
  }

}
