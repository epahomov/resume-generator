package resumes.generators

import org.scalatest.junit.JUnitSuite
import resumes.company.PositionManager.Area.Area
import resumes.generators.Utils.trueFalseDistribution
import resumes.generators.education.EducationUtils

class GeneratorsTest extends JUnitSuite  {

  lazy val areaDefined = trueFalseDistribution(1, 1)

  def getArea(): Option[Area] = {
    areaDefined.sample() match {
      case true => Some(EducationUtils.randomAreaGenerator.sample())
      case false => None
    }
  }

}
