package resumes.generators.work

import resumes.company.PositionManager.Area
import resumes.company.PositionManager.Area.Area
import resumes.generators.Utils

import scala.io.Source

object CompanyGenerator {

  lazy val anyCompanyGenerator = {
    val data = Source
      .fromResource("generators/work/companies.txt")
      .getLines()
      .map(line => {
        val pair = line.split("\\$")
        val companyName = pair(0).filter(!_.isDigit)
        val revenue = pair(1).filter(_.isDigit).toInt
        (companyName, revenue)
      }).toList
    Utils.getGeneratorFrequency(data)
  }

  lazy val itCompaniesGenerator = {
    val data = Source
      .fromResource("generators/work/it_companies.txt")
      .getLines()
      .map(line => {
        val pair = line.split(" ")
        val companyName = pair(1)
        val weight = 1000 - pair(0).filter(_.isDigit).toInt
        (companyName, weight)
      }).toList
    Utils.getGeneratorFrequency(data)
  }

  type CompanyName = String

  def generateCompany(area: Option[Area]): CompanyName = {
    if (area.isDefined && area.get.equals(Area.Computer_Science)) {
      itCompaniesGenerator.sample()
    } else {
      anyCompanyGenerator.sample()
    }
  }


  def main(args: Array[String]): Unit = {
    (0 to 100).foreach(_ => {
      //println(generateCompany(Some(Area.Computer_Science)))
      println(generateCompany(None))
    })
  }

}
