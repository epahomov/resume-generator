package resumes.generators.person

import net.liftweb.json.Extraction.decompose
import net.liftweb.json.JsonAST.prettyRender
import org.junit.{Ignore, Test}
import org.scalatest.junit.JUnitSuite
import resumes.run.Instances
import resumes.MongoDB.formats
import resumes.company.PositionManager.{Area, ExperienceLevel, Position}

class PersonGeneratorTest extends JUnitSuite {


  @Ignore
  def testGeneratingPerson2(): Unit = {

    val position = Instances.positionManager.getPositionById("359ff2b2-5e5a-4619-baa9-dad62ef51533").get.copy(address = Some("San Francisco, CA"))
    println(prettyRender(decompose(position)))
    (0 to 20).foreach(_ => {
      println("--------")
      val person = PersonGenerator.generatePerson(position)
      println(prettyRender(decompose(person)))
    })
  }

  @Ignore
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
