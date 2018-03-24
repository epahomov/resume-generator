package resumes

import resumes.generators.PersonGenerator.{Candidate, Person}

package object application_submitter {


  case class Application(candidate: Candidate, positionId: String)

}
//zSwptwTux8b5flq2