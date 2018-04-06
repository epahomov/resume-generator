package resumes.generators.work

import org.joda.time.DateTime
import resumes.company.PositionManager.Area.Area
import resumes.company.PositionManager.{Area, ExperienceLevel, Position}
import resumes.generators.Utils
import resumes.generators.education.EducationGenerator
import resumes.generators.education.EducationGenerator.Education
import resumes.generators.work.EmploymentGenerator.Employment

import scala.util.Random

object InternshipGenerator {

  private lazy val hadInternship = {
    val distribution = List(
      (true, 1),
      (false, 4)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  def generateInternships(educations: List[Education],
                          area: Option[Area]
                         ): List[Employment] = {

    val educationStartYear = educations.map(_.startYear).min
    val educationEndYear = educations.map(_.endYear).max

    (educationStartYear + 1 to educationEndYear - 1).flatMap(summer => {
      if (hadInternship.sample()) {
        val company = CompanyGenerator.generateCompany(area)
        val role = RoleGenerator.generateRole(area, ExperienceLevel.Freshly_Graduate)
        val start = new DateTime(summer, 5 + Random.nextInt(2), 1, 1, 1).toDate
        val end = new DateTime(summer, 8 + Random.nextInt(2), 1, 1, 1).toDate
        val description = ""
        Some(
          Employment(
            company = company,
            role = role,
            start = start,
            end = end,
            description = description
          )
        )
      } else {
        None
      }
    }).toList
  }

}
