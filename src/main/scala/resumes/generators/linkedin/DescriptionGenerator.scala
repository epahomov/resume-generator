package resumes.generators.linkedin

import resumes.company.CompanyManager.Companies.Companies
import resumes.run.Instances

import scala.util.Try

object DescriptionGenerator {

  def availableRoles(company: Companies) = {
    Instances.linkedInRoleManager.getNotUsedRole(company)
  }

  case class Description(id: String, description: String)

  private def normalize(s: String) = {
    s.toLowerCase.filter(_.isLetter)
  }

  def getDescription(roleName: String,
                     skills: List[String],
                     company: Companies
                    ): Try[Description] = {
    Try {
      val rolesToChoose = availableRoles(company)
      val rolesWithScores = rolesToChoose.map(role => {
        var score = 0
        if (normalize(role.employment.role.getOrElse("")).contains(normalize(roleName))) {
          score += 10
        }
        (role, score)
      })
      val max = rolesWithScores.map(_._2).max
      val role = rolesWithScores
        .find(x => x._2.equals(max))
        .get._1
      Description(role.id, role.employment.description.get)
    }
  }
}
