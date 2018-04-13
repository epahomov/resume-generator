package resumes.generators.person

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import resumes.company.PositionManager.{Area, ExperienceLevel, Position}

class PersonGeneratorTest extends JUnitSuite {

  @Test
  def testGeneratingPerson() = {
    (0 to 100).foreach(_ => {
      val position = Position (
        company = null,
        url = null,
        area = Some(Area.Computer_Science.toString),
        experienceLevel = Some(ExperienceLevel.Senior.toString)
      )
      val person = PersonGenerator.generatePerson(position)
      println("--------------")
      person.education.foreach(education => {
        println(education)
      })

      person.workExperience.foreach(employment => {
        println(employment)
      })
    })
  }
}
