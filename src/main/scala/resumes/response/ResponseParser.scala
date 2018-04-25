package resumes.response

import org.apache.logging.log4j.LogManager
import resumes.applications.ApplicationManager
import resumes.company.CompanyManager.Companies
import resumes.response.result.identifiers.{IBMResultIdentifier, SalesForceResultIdentifier}

class ResponseParser(applicationManager: ApplicationManager) {

  private val logger = LogManager.getLogger(this.getClass)

  private val companyToIdentifier = Map(
    Companies.IBM.toString -> IBMResultIdentifier,
    Companies.SalesForce.toString -> SalesForceResultIdentifier
  )

  def parse() = {
    applicationManager
      .getAllApplicationsWithUnknownResponse()
      .foreach(application => {
        application.response match {
          case Some(response) => {
            val resultIdentifier = companyToIdentifier.get(application.company).get
            response.messages.map(message => {
              resultIdentifier.result(message)
            }).find(_.isDefined) match {
              case Some(result) => {
                val resultResponse = response.copy(decision = result.get.toString)
                applicationManager.updateResponse(application.id, resultResponse)
              }
              case None => {
                if (response.messages.size > 0) {
                  logger.error(s"For application ${application.id} could not identify result." +
                    s" Messages: \n ${response.messages.mkString("\n")}")
                }
              }
            }
          }
          case None => {
            logger.info(s"No response for application - ${application.id}")
          }
        }
      })
  }
}
