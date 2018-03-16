package resumes.generators.name

import resumes.generators.name.FirstNameGenerator.Gender.Gender
import resumes.generators.name.FirstNameGenerator.{Gender, generateRandomFirstName}
import resumes.generators.name.LastNameGenerator.generateLastName

object NameGenerator {

  case class Name(firstName: String, lastName: String)

  def generateRandomName(gender: Gender) = {
    val firstName = generateRandomFirstName(gender)
    val lastName = generateLastName()
    Name(firstName, lastName)
  }

  def main(args: Array[String]): Unit = {
    println("Male Names:")
    for (i <- 1 to 100) {
      println(generateRandomName(Gender.Male))
    }
    println("Female names:")
    for (i <- 1 to 100) {
      println(generateRandomName(Gender.Female))
    }
  }

}
