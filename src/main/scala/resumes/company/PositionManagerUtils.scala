package resumes.company

import resumes.MongoDB
import resumes.company.PositionManager.Position

import scala.io.Source

object PositionManagerUtils {

  def uploadPositions(company: String, path: String, manager: PositionManager) = {
    val positions = Source
      .fromResource(path)
      .getLines()
      .map(line => {
        Position(company, line)
      }).toList
    manager.uploadPositions(positions)
  }

  def main(args: Array[String]): Unit = {
    val company = "ibm"
    val path = "applications/ibm/ibm_jobs.txt"
    val manager = new PositionManager(MongoDB.database)
    uploadPositions(company, path, manager)
  }
}
