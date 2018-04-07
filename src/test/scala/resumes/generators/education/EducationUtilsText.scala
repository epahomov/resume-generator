package resumes.generators.education

import java.util.NoSuchElementException

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import resumes.company.PositionManager.Area

class EducationUtilsText extends JUnitSuite {

  @Test
  def test(): Unit = {
    Area.values.foreach(area => {
      (0 to 100).foreach(_ => {
        val major = EducationUtils.getRandomMajorByArea(area)
        EducationUtils.getRandomMajor()
        try {
          val someArea = EducationUtils.getAreaByMajor(major)
        } catch {
          case e: NoSuchElementException => throw new RuntimeException(s"Could not find area for major $major", e)
        }
      })
    })
  }

}
