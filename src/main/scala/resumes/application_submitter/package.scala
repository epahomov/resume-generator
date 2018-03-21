package resumes

import resumes.generators.PersonGenerator.{Candidate, Person}

package object application_submitter {

  System.setProperty("webdriver.gecko.driver", "/Users/macbook/Downloads/geckodriver")

  case class Application(candidate: Candidate, positionId: String)

}
