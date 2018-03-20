package resumes.generators.name

import resumes.generators.name.FirstNameGenerator.Gender.Gender
import resumes.generators.name.FirstNameGenerator.Origin.Origin
import resumes.generators.name.FirstNameGenerator.{Gender, Origin, generateRandomFirstName}
import resumes.generators.name.LastNameGenerator.generateLastName

object NameGenerator {

  case class Name(firstName: String, lastName: String)

  def generateRandomName(gender: Gender, origin: Origin) = {
    val firstName = generateRandomFirstName(gender, origin)
    val lastName = generateLastName(origin)
    Name(firstName, lastName)
  }

  def main(args: Array[String]): Unit = {
    println("Male Names:")
    for (i <- 1 to 100) {
      println(generateRandomName(Gender.Male, Origin.India))
    }
    println("Female names:")
    for (i <- 1 to 100) {
      println(generateRandomName(Gender.Female, Origin.India))
    }
  }

}
