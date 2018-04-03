package resumes.company

import resumes.MongoDB
import resumes.company.CompanyManager.{Companies, Company}
import resumes.company.PositionManager.{Area, ExperienceLevel, Position}

import scala.io.Source

object PositionManagerUtils {

  def uploadPositions(company: String, path: String, manager: PositionManager) = {
    val positions = Source
      .fromResource(path)
      .getLines()
      .map(line => {
        Position(company = company,
          url = line,
          area = Some(Area.Finance),
          experienceLevel = Some(ExperienceLevel.Freshly_Graduate)
        )
      }).toList
    manager.uploadPositions(positions)
  }

  def main(args: Array[String]): Unit = {
    val company = Companies.IBM.toString
    val path = "positions"
    val manager = new PositionManager(MongoDB.database)
    uploadPositions(company, path, manager)
  }
}
