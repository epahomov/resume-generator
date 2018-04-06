package resumes.applications.submitters

import resumes.MongoDB
import resumes.applications.DummyApplication
import resumes.company.CompanyManager.Companies
import resumes.company.PositionManager
import resumes.run.Instances

import scala.util.{Failure, Random, Success}

abstract class SubmitterHelper {

  val submitter: Submitter

  private lazy val positionManager = new PositionManager(MongoDB.database)

  def almostSubmitOneDummyApplication(): Unit = {
    val position = positionManager.getRandomPosition(submitter.company)
    println(position)
    val dummyApplication = DummyApplication.veryPlainApplication(submitter.company.toString, position.url)
    submitter.submit(dummyApplication, false) match {
      case Success(_) =>
      case Failure(e) => throw new RuntimeException(e)
    }
  }

  def almostSubmitNDummyApplication(n: Int = 15): Unit = {
    (0 to n).foreach(_ => {
      almostSubmitOneDummyApplication()
    })
  }

  def almostSubmitNotSoDummyApplication(): Unit = {
    val application = Instances
      .applicationManager
      .createApplication(submitter.company)
    val safeApplication = application.copy(email = Random.nextInt(10000) + "blabla@gmail.com")
    submitter.submit(safeApplication, false)
  }

  def almostSubmitNNotSoDummyApplication(n: Int = 15): Unit = {
    (0 to n).foreach(_ => {
      almostSubmitNotSoDummyApplication()
    })
  }
}
