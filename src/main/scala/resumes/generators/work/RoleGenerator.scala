package resumes.generators.work

import resumes.company.PositionManager.Area.Area
import resumes.company.PositionManager.ExperienceLevel.ExperienceLevel
import resumes.company.PositionManager.{Area, ExperienceLevel}
import resumes.generators.Utils

import scala.util.Random

object RoleGenerator {

  object Role extends Enumeration {
    type Role = Value
    val Intern = Value("Intern")
    val Software_developer = Value("Software developer")
    val Software_engineer = Value("Software engineer")
    val Engineering_intern = Value("Engineering intern")
    val QA = Value("QA")
    val QualityAssurance = Value("Quality assurance")
    val Computer_Systems_Analyst = Value("Computer Systems Analyst")
    val Computer_Systems_Administrator = Value("Computer_Systems_Administrator")
    val DataEngineer = Value("Data Engineer")
    val Database_Administrator = Value("Database Administrator")
    val Web_Developer = Value("Web Developer")
  }

  val computerScienceFreshlyGraduatePositions = List(
    Role.Software_developer,
    Role.Software_engineer,
    Role.Engineering_intern,
    Role.QA,
    Role.QualityAssurance,
    Role.Computer_Systems_Analyst,
    Role.DataEngineer,
    Role.Computer_Systems_Administrator,
    Role.Database_Administrator,
    Role.Web_Developer
  )

  lazy val isJunior = {
    val distribution = List(
      (true, 5),
      (false, 3)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  def generateRole(area: Option[Area], experienceLevel: ExperienceLevel): String = {
    experienceLevel match {
      case ExperienceLevel.Freshly_Graduate => {
        area match {
          case Some(someArea) => {
            val role =
              someArea match {
                case Area.Computer_Science => {
                  computerScienceFreshlyGraduatePositions(
                    Random.nextInt(computerScienceFreshlyGraduatePositions.size) - 1
                  )
                }
              }
            if (isJunior.sample()) {
              "Junior " + role.toString
            } else {
              role.toString
            }
          }
          case None => Role.Intern.toString
        }
      }
    }
  }

}
