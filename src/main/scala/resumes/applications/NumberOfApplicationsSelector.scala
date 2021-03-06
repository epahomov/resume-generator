package resumes.applications

import org.joda.time.{DateTime, Days}
import resumes.company.CompanyManager
import resumes.company.CompanyManager.Companies

import scala.util.Random


class NumberOfApplicationsSelector(companyManager: CompanyManager) {

  def getNumberOfApplications(company: Companies.Value, current: DateTime = new DateTime()): Int = {
    val start = new DateTime(companyManager.getCompany(company).startDate)
    getNumberOfApplications(Days.daysBetween(start, current).getDays)

  }

  private def getNumberOfApplications(differenceInDays: Int): Int = {
    if (differenceInDays < 4) {
      10
    } else {
      val applications = Math.log(Math.pow(differenceInDays, 5)).round.toInt
      applications + Random.nextInt(Math.max(applications / 3, 1))
    }
  }

}
