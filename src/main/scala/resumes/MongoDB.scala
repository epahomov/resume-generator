package resumes

import com.mongodb.client.{MongoCollection, MongoDatabase}
import com.mongodb.{MongoClient, MongoClientURI}
import net.liftweb.json.Extraction.decompose
import net.liftweb.json.JsonAST.prettyRender
import net.liftweb.json.ext.EnumSerializer
import org.bson.Document
import resumes.generators.name.FirstNameGenerator.{Gender, Origin}

import scala.collection.JavaConverters._

object MongoDB {

  def createCollectionIfNotExists(collectionName: String, mongoDatabase: MongoDatabase) = {
    if (mongoDatabase.listCollectionNames().asScala.toSet.contains(collectionName)) {
      mongoDatabase.createCollection(collectionName)
    }
  }

  implicit val formats = net.liftweb.json.DefaultFormats + new EnumSerializer(Gender)  + new EnumSerializer(Origin)

  def insertIntoCollection[T](values: Seq[T], mongoCollection: MongoCollection[Document]) = {
    val toInsert = values.map(x => {
      Document.parse(prettyRender(decompose(x)))
    }).asJava
    mongoCollection.insertMany(toInsert)
  }

  private val uri = new MongoClientURI("mongodb+srv://epakhomov:Lk32Lk32Lk32@cluster0-9iykb.mongodb.net/test")
  val mongoClient = new MongoClient(uri)
  val database = mongoClient.getDatabase("resume")

}