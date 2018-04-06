package resumes.generators.work

import org.junit.Test
import resumes.company.PositionManager.ExperienceLevel
import resumes.generators.{GeneratorsTest, Utils}

class RoleGeneratorTest extends GeneratorsTest {

  lazy val experienceLevelGenerator = {
    val data = ExperienceLevel
      .values
      .map(level => (level, 1))
      .toList
    Utils.getGeneratorFrequency(data)
  }


  @Test
  def testGenerateRole(): Unit = {

    (0 to 100).foreach(_ => {
      val area = getArea()
      val experienceLevel = experienceLevelGenerator.sample()
      println(s"Area: $area, experienceLevel: $experienceLevel")
      println(RoleGenerator.generateRole(area, experienceLevel))
    })
  }
}
