package resumes.run

import resumes.applications.NumberOfApplicationsSelector
import Instances._
import resumes.applications.submitters.IBMApplicationSubmitter

object Submit {
  def main(args: Array[String]): Unit = {
    val numberOfApplicationsSelector = new NumberOfApplicationsSelector(companyManager)
    val submitter = new IBMApplicationSubmitter(applicationManager, numberOfApplicationsSelector)
    submitter.submitAllNecessaryApplicationsForToday()
  }

}
