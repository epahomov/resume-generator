package resumes.application_submitter

import org.joda.time.{DateTime, Days}
import org.joda.time.format.DateTimeFormat

import scala.io.Source
import scala.util.Random


object NumberOfApplicationsSelector {

  val formatter = DateTimeFormat.forPattern("dd/MM/yyyy")

  def getNumberOfApplications(company: String, current: DateTime = new DateTime()): Int = {
    val startDateTxt = Source
      .fromResource(s"applications/$company/start_date.txt")
      .getLines()
      .mkString("")
    val start = formatter.parseDateTime(startDateTxt)
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
