package resumes.generators.work

import resumes.company.PositionManager.Area.Area
import resumes.company.PositionManager.{Area, ExperienceLevel}
import resumes.company.PositionManager.ExperienceLevel.ExperienceLevel
import resumes.generators.Utils
import resumes.generators.Utils._

object RoleGenerator {

  private lazy val isLevelPrefix = trueFalseDistribution(1, 5)

  private lazy val levelSpecificPosition = trueFalseDistribution(1, 5)

  def generateRole(area: Option[Area], experienceLevel: ExperienceLevel): String = {
    val fileName = levelSpecificPosition.sample() match {
      case true => {
        experienceLevel match {
          case ExperienceLevel.Freshly_Graduate => "freshly_graduate.txt"
          case ExperienceLevel.Beginner => "all.txt"
          case ExperienceLevel.Middle => "all.txt"
          case ExperienceLevel.Senior => "all.txt" // to do
        }
      }
      case false => {
        "all.txt"
      }
    }

    val areaDirectory = area match {
      case Some(someArea) => {
        Utils.areaToFileSystemName.get(someArea).get
      }
      case None => "default"
    }
    val path = s"generators/work/roles/$areaDirectory/$fileName"
    val role = generatorFromFile(path).sample()
    if (isLevelPrefix.sample()) {
      experienceLevel match {
        case ExperienceLevel.Freshly_Graduate => "Junior " + role
        case ExperienceLevel.Beginner => "Junior " + role
        case ExperienceLevel.Middle => role
        case ExperienceLevel.Senior => "Senior " + role
      }
    } else {
      role
    }
  }

}
