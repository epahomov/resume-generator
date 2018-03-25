package resumes.generators

import org.junit.Test
import resumes.MongoTest

class PeopleManagerTest extends MongoTest {

  @Test
  def testGenerated(): Unit = {
    val personManager = new PeopleManager(_mongo_database)
    personManager.storePeople(10)
    val person = personManager.getRandomPerson().get
    assert(person.name.lastName.size > 0)
    assert(personManager.getPersonById(person.id).get.name.lastName === person.name.lastName)
    personManager.deletePerson(person)
    assert(personManager.getPersonById(person.id) === None)
  }

}
