package resumes.generators.work

import org.junit.Test
import resumes.company.PositionManager.{Area, Position}
import resumes.generators.GeneratorsTest
import resumes.generators.education.{EducationGenerator, EducationUtils}
import resumes.generators.work.InternshipGenerator.generateInternships

class InternshipGeneratorTest extends GeneratorsTest {

  @Test
  def testGenerateInternships(): Unit = {
    var sum = 0
    (0 to 100).foreach(_ => {
      val area = Some(EducationUtils.randomAreaGenerator.sample())
      val position = new Position(company = null, url = null, area = area.map(x => x.toString))
      val education = EducationGenerator.generateEducation(position)
      println(s"Area: $area, position: $position, education: $education")
      val internships = generateInternships(education, area)
      println(internships)
      sum += internships.size
    })
    assert(sum > 30)
  }

}
