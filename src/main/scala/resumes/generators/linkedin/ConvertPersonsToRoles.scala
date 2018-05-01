package resumes.generators.linkedin

import org.apache.logging.log4j.LogManager
import resumes.generators.linkedin.LinkedInRoleManager.LinkedInRole
import resumes.run.Instances

object ConvertPersonsToRoles {

  protected val logger = LogManager.getLogger(this.getClass)

  def convertPersonsToRoles() = {
    val personManager = Instances.linkedInManager
    val roleManager = Instances.linkedInRoleManager
    val urlsToConvert = {
      val existing = roleManager.peopleUrlsProcessed()
      val available = personManager.getUrls()
      available.filter(url => !existing.contains(url))
    }
    logger.info(s"Found ${urlsToConvert.size} urls to convert")
    urlsToConvert.foreach(url => {
      val person = personManager.getPerson(url)
      person
        .employments
        .foreach(employment => {
          val role = LinkedInRole(personUrl = person.url,
            employment = employment,
            companiesInWhichBeenUsed = List.empty
          )
          roleManager.addRole(role)
        })
    })
  }
}
