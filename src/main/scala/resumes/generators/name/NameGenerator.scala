package resumes.generators.name

import resumes.generators.name.FirstNameGenerator.Gender.Gender
import resumes.generators.name.FirstNameGenerator.generateRandomFirstName
import resumes.generators.name.LastNameGenerator.generateLastName

object NameGenerator {

  case class Name(firstName: String, lastName: String)

  def generateRandomName(gender: Gender) = {
    val firstName = generateRandomFirstName(gender)
    val lastName = generateLastName()
    Name(firstName, lastName)
  }

}
