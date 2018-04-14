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
    val manager = new PositionManager(MongoDB.database)
    val position = Position(
      company = Companies.IBM.toString,
      url = "560708",
      requiredMajor = None,
      area = Some(Area.Computer_Science.toString),
      experienceLevel = Some(ExperienceLevel.Freshly_Graduate.toString),
      previousPosition = None,
      popularity = Some(100),
      minimumDegreeNecessary = Some(Degree.Master.toString)
    )
    manager.uploadPositions(List(position))
    //uploadPositions(company, path, manager)
  }
}
