package resumes.generators.work

import org.joda.time.DateTime
import resumes.company.PositionManager.Area.Area
import resumes.company.PositionManager.ExperienceLevel
import resumes.generators.Utils
import resumes.generators.education.EducationGenerator.Education
import resumes.generators.person.SkillsGenerator
import resumes.generators.work.EmploymentGenerator.Employment

import scala.util.Random

object InternshipGenerator {

  private lazy val hadInternship = Utils.trueFalseDistribution(forTrue = 1, forFalse = 4)

  def generateInternships(educations: List[Education],
                          area: Option[Area]
                         ): List[Employment] = {

    val educationStartYear = educations.map(_.startYear).min
    val educationEndYear = educations.map(_.endYear).max
    val formatter =  SkillsGenerator.skillsFomatter()
    (educationStartYear + 1 to educationEndYear - 1).flatMap(summer => {
      if (hadInternship.sample()) {
        val company = CompanyGenerator.generateCompany(area)
        val role = RoleGenerator.generateRole(area, ExperienceLevel.Freshly_Graduate)
        val start = new DateTime(summer, 5 + Random.nextInt(2), 1, 1, 1).toDate
        val end = new DateTime(summer, 8 + Random.nextInt(2), 1, 1, 1).toDate
        val description = ""
        val skills = SkillsGenerator.getSkillsList(role, None, false, area.get)
        val skillsFormatted = formatter(skills)
        Some(
          Employment(
            company = company,
            role = role,
            start = start,
            end = end,
            description = description,
            internship = Some(true),
            skills = Some(skills),
            skillsFormatted = Some(skillsFormatted)
          )
        )
      } else {
        None
      }
    }).toList
  }

}
