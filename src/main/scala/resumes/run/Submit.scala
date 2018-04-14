package resumes.run

import resumes.applications.NumberOfApplicationsSelector
import Instances._
import resumes.applications.submitters.{IBMApplicationSubmitter, SalesForceApplicationSubmitter}

object Submit {
  def main(args: Array[String]): Unit = {
    val numberOfApplicationsSelector = new NumberOfApplicationsSelector(companyManager)
    val submitters = List(
      new IBMApplicationSubmitter(applicationManager, numberOfApplicationsSelector)
    )
    submitters.foreach(submitter => {
      submitter.submitAllNecessaryApplicationsForToday()
    })
  }

}
