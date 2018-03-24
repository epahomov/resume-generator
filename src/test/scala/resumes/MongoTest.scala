package resumes

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{MongodExecutable, MongodProcess, MongodStarter}
import de.flapdoodle.embed.process.runtime.Network
import org.junit.{After, Before}
import org.scalatest.junit.JUnitSuite

class MongoTest extends JUnitSuite {
  val starter = MongodStarter.getDefaultInstance()
  var _mongodExe: MongodExecutable = null
  var _mongod: MongodProcess = null
  var _mongo: MongoClient = null
  var _mongo_database: MongoDatabase = null

  @Before
  def before() = {
    val port = Network.getFreeServerPort()

    _mongodExe = starter.prepare(new MongodConfigBuilder()
      .version(Version.Main.V3_6)
      .net(new Net("localhost", port, Network.localhostIsIPv6()))
      .build())
    _mongod = _mongodExe.start()
    _mongo = new MongoClient("localhost", port)
    _mongo_database = _mongo.getDatabase("test")
  }


  @After
  def after() = {
    _mongod.stop()
    _mongodExe.stop()
  }

}
