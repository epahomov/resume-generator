package resumes.application_submitter

import resumes.application_submitter.ApplicationManager.Application

trait Submitter {

  def submit(application: Application)

}
