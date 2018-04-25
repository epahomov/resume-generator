package resumes.generators.linkedin

import org.junit.{Before, Test}
import resumes.MongoTest
import resumes.generators.linkedin.LinkedInParser.LinkedInPerson

class LinkedInPersonManagerTest extends MongoTest {

  var linkedINPersons: LinkedInPersonManager = null

  @Before
  def start() = {
    linkedINPersons = new LinkedInPersonManager(_mongo_database)
  }

  @Test
  def testMarkAsUsed(): Unit = {
    val url = "123"
    val person = LinkedInPerson(
      url = url,
      name = Some("sfsf"),
      education = List.empty,
      employments = List.empty
    )
    assert(linkedINPersons.isPersonExist(url) === false)
    linkedINPersons.uploadPerson(person)
    assert(linkedINPersons.isPersonExist(url) === true)
    assert(linkedINPersons.getPerson(url) === person)

  }
}
