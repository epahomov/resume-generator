package resumes.generators.work

import java.util.Date

import org.joda.time.{DateTime, Period}
import resumes.company.PositionManager.ExperienceLevel.ExperienceLevel
import resumes.company.PositionManager.{Area, ExperienceLevel}
import resumes.generators.Utils.generatorFromFile

import scala.collection.mutable.ListBuffer
import scala.util.Random

object EmploymentGenerator {

  case class Employment(start: Date,
                        end: Date,
                        company: String,
                        description: String,
                        role: String,
                        current: Option[Boolean] = Some(false),
                        experienceLevel: Option[ExperienceLevel] = None
                       )

  def getGraduationYear(experienceLevel: ExperienceLevel): Int = {
    if (experienceLevel.equals(ExperienceLevel.Freshly_Graduate)) {
      new DateTime().getYear
    } else {
      val minimumYearsAfterCollege = experienceLevel match {
        case ExperienceLevel.Beginner => 1
        case ExperienceLevel.Middle => 3
        case ExperienceLevel.Senior => 7
      }
      val yearsAfterCollege = minimumYearsAfterCollege + Random.nextInt(4)
      val graduationYear = new DateTime().plusYears(-1 * yearsAfterCollege).year().get()
      graduationYear
    }
  }

  def generateEmployment(area: Area.Value, graduationYear: Int): List[Employment] = {

    var currentEnd = new DateTime()
    val graduation = new DateTime(graduationYear, 5 + Random.nextInt(4), Random.nextInt(28) + 1, 1, 1)
    val result = new ListBuffer[Employment]

    var current = true
    while (currentEnd.compareTo(graduation) > 0) {
      val yearsOnThisPlace = generatorFromFile("generators/work/employmentPerEmployee.txt").sample().toInt
      val end = currentEnd
      val start = latest(currentEnd.plusDays(-1 * (yearsOnThisPlace * 365 + Random.nextInt(150))), graduation)
      val company = CompanyGenerator.generateCompany(Some(area))
      val description = ""
      val experienceLevel = new Period(graduation, start).getYears match {
        case x if x > 6 => ExperienceLevel.Senior
        case x if x <= 6 && x > 2 => ExperienceLevel.Middle
        case x if x <= 2 => ExperienceLevel.Beginner
      }
      val role = RoleGenerator.generateRole(Some(area), experienceLevel)
      val employment = Employment(
        start = start.toDate,
        end = end.toDate,
        company = company,
        description = description,
        role = role,
        current = Some(current),
        experienceLevel = Some(experienceLevel)
      )
      result += employment
      current = false
      currentEnd = start
    }
    result.toList
  }


  def latest(a: DateTime, b: DateTime): DateTime = {
    if (a.compareTo(b) > 0) a
    else b
  }

}
