package resumes.generators.person

import resumes.company.PositionManager.{ExperienceLevel, Position}
import resumes.company.PositionManager.ExperienceLevel.ExperienceLevel
import resumes.generators.Utils
import resumes.generators.person.PersonGenerator.Comment

import scala.util.Random

object ExperienceLevelGenerator {

  val experienceLevelMatch = Utils.trueFalseDistribution(forTrue = 5, forFalse = 1)

  def generateExperienceLevel(position: Position): (ExperienceLevel, Comment) = {
    val level = position
      .experienceLevel
      .map(ExperienceLevel.withName(_))
      .getOrElse(ExperienceLevel.Freshly_Graduate)

    experienceLevelMatch.sample() match {
      case true => {
        (level, "Experience level as specified")
      }
      case false => {
        level match {
          case ExperienceLevel.Freshly_Graduate => (ExperienceLevel.Beginner, "Experience level upgraded")
          case ExperienceLevel.Senior => (ExperienceLevel.Middle, "Experience level downgraded")
          case ExperienceLevel.Beginner => {
            if (Random.nextBoolean()) (ExperienceLevel.Middle, "Experience level upgraded") else (ExperienceLevel.Freshly_Graduate, "Experience level downgraded")
          }case ExperienceLevel.Middle => {
            if (Random.nextBoolean()) (ExperienceLevel.Beginner, "Experience level downgraded") else (ExperienceLevel.Senior, "Experience level upgraded")
          }
        }
      }
    }
  }
}
