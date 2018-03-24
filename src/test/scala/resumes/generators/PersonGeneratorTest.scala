package resumes.generators

import org.junit.Test
import resumes.MongoTest

class PersonGeneratorTest extends MongoTest {

  @Test
  def testGenerated(): Unit = {
    val personManager = new PeopleManager(_mongo_database)
    personManager.storePeople(10)
    val person = personManager.getRandomPerson()
    assert(person.get.name.lastName.size > 0)
  }

}
