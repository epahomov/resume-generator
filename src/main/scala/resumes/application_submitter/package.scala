package resumes

import resumes.generators.PeopleManager.Person

package object application_submitter {

  case class Application(person: Person, positionId: String)

}