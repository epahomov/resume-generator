package resumes.generators.name

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import resumes.generators.name.FirstNameGenerator.{Gender, Origin}

class LastNameGeneratorTest extends JUnitSuite {

  @Test
  def testGenerateLastName() = {
    Origin.values.foreach(origin => {
      (0 to 100).foreach(_ => {
        LastNameGenerator.generateLastName(origin)
      })
    })
  }

}
