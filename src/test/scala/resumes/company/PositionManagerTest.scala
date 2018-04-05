package resumes.company

import org.junit.Test
import resumes.MongoTest
import resumes.company.CompanyManager.Companies
import resumes.company.PositionManager.Position

class PositionManagerTest extends MongoTest {

  @Test
  def testPositionManager(): Unit = {
    val manager = new PositionManager(_mongo_database)
    val positions = List(
      Position("ibm", "1234"),
      Position("ibm", "5346"),
      Position("ibm", "453"),
      Position("google", "476575"),
      Position("google", "457656")
    )

    def id(index: Int): String = positions(index).id

    manager.uploadPositions(positions)
    assert(manager.getPositionById(id(0)).get.url === positions(0).url)
    assert(manager.getRandomPosition(Companies.IBM).company === "ibm")
    assert(manager.getPositionById(id(1)).get.active === true)
    for (i <- 0 to 3) {
      manager.failedToApplyToPosition(id(1))
    }
    assert(manager.getPositionById(id(1)).get.active === false)
    for (i <- 0 to 3) {
      manager.failedToApplyToPosition(id(0))
    }
    for (i <- 0 to 3) {
      assert(manager.getRandomPosition(Companies.IBM).id === id(2))
    }
    manager.successfullyAppliedForPosition(id(0))
    assert(manager.getPositionById(id(0)).get.failedAttemptsToApply === 0)
  }
}
