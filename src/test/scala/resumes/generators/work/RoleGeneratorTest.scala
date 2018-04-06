package resumes.generators.work

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import resumes.company.PositionManager.Area.Area
import resumes.company.PositionManager.{Area, ExperienceLevel}
import resumes.generators.Utils
import resumes.generators.Utils.trueFalseDistribution

class RoleGeneratorTest extends JUnitSuite {

  lazy val experienceLevelGenerator = {
    val data = ExperienceLevel
      .values
      .map(level => (level, 1))
      .toList
    Utils.getGeneratorFrequency(data)
  }

  lazy val areaGenerator = {
    val data = Area
      .values
      .map(area => (area, 1))
      .toList
    Utils.getGeneratorFrequency(data)
  }

  lazy val areaDefined = trueFalseDistribution(1, 1)

  def getArea(): Option[Area] = {
    areaDefined.sample() match {
      case true => Some(areaGenerator.sample())
      case false => None
    }
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
