package resumes.generators

import org.scalatest.junit.JUnitSuite
import resumes.company.PositionManager.Area
import resumes.company.PositionManager.Area.Area
import resumes.generators.Utils.trueFalseDistribution

class GeneratorsTest extends JUnitSuite  {

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

}
