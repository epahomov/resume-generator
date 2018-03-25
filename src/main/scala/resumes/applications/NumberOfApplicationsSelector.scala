package resumes.applications

import org.joda.time.{DateTime, Days}
import resumes.company.CompanyManager

import scala.util.Random


class NumberOfApplicationsSelector(companyManager: CompanyManager) {

  def getNumberOfApplications(company: String, current: DateTime = new DateTime()): Int = {
    val start = new DateTime(companyManager.getCompany(company).startDate)
    getNumberOfApplications(Days.daysBetween(start, current).getDays)

  }

  private def getNumberOfApplications(differenceInDays: Int): Int = {
    if (differenceInDays < 4) {
      6
    } else {
      val applications = Math.log(Math.pow(differenceInDays, 5)).round.toInt
      applications + Random.nextInt(Math.max(applications / 3, 1))
    }
  }

}
