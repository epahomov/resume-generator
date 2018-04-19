package resumes.generators.work

import java.util.Date

import org.joda.time.{DateTime, Period}
import resumes.company.PositionManager.ExperienceLevel.ExperienceLevel
import resumes.company.PositionManager.{Area, ExperienceLevel}
import resumes.generators.Utils
import resumes.generators.Utils.generatorFromFile
import resumes.generators.person.PersonGenerator.Comment
import resumes.generators.person.SkillsGenerator

import scala.collection.mutable.ListBuffer
import scala.util.Random

object EmploymentGenerator {

  case class Employment(start: Date,
                        end: Date,
                        company: String,
                        description: String,
                        role: String,
                        current: Option[Boolean] = Some(false),
                        experienceLevel: Option[String] = None,
                        internship: Option[Boolean] = None,
                        skills: Option[List[String]] = None,
                        skillsFormatted: Option[String] = None
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


  val lastPositionMatch = Utils.trueFalseDistribution(forTrue = 5, forFalse = 1)

  def generateEmployment(area: Area.Value,
                         graduationYear: Int,
                         previousPosition: Option[String],
                         requiredSkills: Option[List[String]]
                        ): (List[Employment], Comment) = {

    var currentEnd = new DateTime()
    val graduation = new DateTime(graduationYear, 5 + Random.nextInt(4), Random.nextInt(28) + 1, 1, 1)
    val result = new ListBuffer[Employment]

    var current = true
    var comment = ""
    val formatter =  SkillsGenerator.skillsFomatter()
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
      val role = if (current && previousPosition.isDefined && lastPositionMatch.sample()) {
        comment = "Last role taken from position"
        previousPosition.get
      } else if (current && area.equals(Area.Computer_Science)) {
        if (Random.nextBoolean()) "Software developer" else "Software engineer"
      } else {
        comment = "Last role generated randomly"
        RoleGenerator.generateRole(Some(area), experienceLevel)
      }
      val skills = SkillsGenerator.getSkillsList(role, requiredSkills, current, area)
      val skillsFormatted = formatter(skills)
      val employment = Employment(
        start = start.toDate,
        end = end.toDate,
        company = company,
        description = description,
        role = role,
        current = Some(current),
        experienceLevel = Some(experienceLevel.toString),
        internship = Some(false),
        skills = Some(skills),
        skillsFormatted = Some(skillsFormatted)
      )
      result += employment
      current = false
      currentEnd = start
    }
    (result.toList, comment)
  }


  def latest(a: DateTime, b: DateTime): DateTime = {
    if (a.compareTo(b) > 0) a
    else b
  }

}
