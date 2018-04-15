package resumes.generators.name

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import resumes.generators.name.FirstNameGenerator.{Gender, Origin}

class FirstNameGeneratorTest extends JUnitSuite {

  @Test
  def testGenerateFirstName() = {
    Origin.values.foreach(origin => {
      Gender.values.foreach(gender => {
        (0 to 100).foreach(_ => {
          FirstNameGenerator.generateRandomFirstName(gender, origin)
        })
      })
    })
  }

}
