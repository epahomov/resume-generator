package resumes.run

import resumes.generators.linkedin.ConvertPersonsToRoles

object AddMoreRoles {

  def main(args: Array[String]): Unit = {
    ConvertPersonsToRoles.convertPersonsToRoles()
  }
}
