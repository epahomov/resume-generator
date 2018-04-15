package resumes.generators.person

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import resumes.generators.person.PhoneNumberGenerator.generateRandomNumber

class PhoneNumberGeneratorTest extends JUnitSuite {

  @Test
  def testGenerateRandomNumber() = {
    println("Phone number generator:")
    (0 to 100).foreach(_ => {
      println(generateRandomNumber("CA"))
    })
  }

}
