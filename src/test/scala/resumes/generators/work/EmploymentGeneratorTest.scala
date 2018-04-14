package resumes.generators.work

import org.junit.Test
import resumes.company.PositionManager.Area
import resumes.generators.GeneratorsTest

class EmploymentGeneratorTest extends GeneratorsTest {


  @Test
  def testGenerateEmployment() = {
    (0 to 100).foreach(_ => {
      val (employment, _) = EmploymentGenerator.generateEmployment(Area.Computer_Science, 2010, None, None)
      println("--------")
      employment.foreach(e => {
        println(e)
      })
    })

  }


}
