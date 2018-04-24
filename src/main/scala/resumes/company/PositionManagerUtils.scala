package resumes.company

import resumes.MongoDB
import resumes.company.CompanyManager.Companies
import resumes.company.PositionManager.{Area, ExperienceLevel, Position}
import resumes.generators.education.Enums.Degree
import resumes.run.Instances

import scala.io.Source

object PositionManagerUtils {

  def main(args: Array[String]): Unit = {
    val manager = new PositionManager(MongoDB.database)
    val position = Position(
      company = Companies.SalesForce.toString,
      url = "https://salesforce.wd1.myworkdayjobs.com/en-US/External_Career_Site/job/California---San-Francisco/Q3-Engineer_JR10280",
      requiredMajor = None,
      area = Some(Area.Computer_Science.toString),
      experienceLevel = Some(ExperienceLevel.Middle.toString),
      previousPosition = Some("Software Quality Engineer"),
      popularity = Some(100),
      skills = None,//Some(List("Java", "C#", "C++", "Python", "Ruby", "Perl")),
      address = Some("San Francisco, CA"),
      minimumDegreeNecessary = Some(Degree.Bachelor.toString)
    )
    manager.uploadPositions(List(position))
    //uploadPositions(company, path, manager)
  }
}
