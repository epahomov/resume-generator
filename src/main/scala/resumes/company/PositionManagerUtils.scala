package resumes.company

import resumes.MongoDB
import resumes.company.CompanyManager.Companies
import resumes.company.PositionManager.{Area, ExperienceLevel, Position}
import resumes.generators.education.Enums.Degree
import resumes.run.Instances

import scala.io.Source

object PositionManagerUtils {

  def uploadPositions(company: String, path: String, manager: PositionManager) = {
    val positions = Source
      .fromResource(path)
      .getLines()
      .map(line => {
        Position(company = company,
          url = line,
          area = Some(Area.Computer_Science.toString),
          experienceLevel = Some(ExperienceLevel.Freshly_Graduate.toString)
        )
      }).toList
    manager.uploadPositions(positions)
  }

  def main(args: Array[String]): Unit = {
    Instances.positionManager.updateSkillsByUrl("81538", skills = List(
      "Python",
      "Go",
      "Swift",
      "Ruby",
      "ASP.NET"
    ))
  }
}
