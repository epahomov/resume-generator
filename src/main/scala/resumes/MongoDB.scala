package resumes

import com.mongodb.client.{MongoCollection, MongoDatabase}
import com.mongodb.{MongoClient, MongoClientURI}
import net.liftweb.json.ext.EnumSerializer
import org.bson.Document
import resumes.Utils._
import resumes.company.PositionManager.{Area, ExperienceLevel}
import resumes.generators.education.Enums.Major
import resumes.generators.name.FirstNameGenerator.{Gender, Origin}

import scala.collection.JavaConverters._

object MongoDB {

  def createCollectionIfNotExists(collectionName: String, mongoDatabase: MongoDatabase) = {
    if
    (!mongoDatabase.listCollectionNames().asScala.toSet.contains(collectionName)) {
      mongoDatabase.createCollection(collectionName)
    }
  }

  implicit val formats = net.liftweb.json.DefaultFormats +
    new EnumSerializer(Gender) +
    new EnumSerializer(Origin) +
    new EnumSerializer(Major) +
    new EnumSerializer(ExperienceLevel) +
    new EnumSerializer(Area)

  def insertValueIntoCollection[T](value: T, mongoCollection: MongoCollection[Document]) = {
    insertIntoCollection(List(value), mongoCollection)
  }

  def insertIntoCollection[T](values: Seq[T], mongoCollection: MongoCollection[Document]) = {
    val toInsert = values.map(toDoc).asJava
    mongoCollection.insertMany(toInsert)
  }

  private val uri = new MongoClientURI("mongodb+srv://epakhomov:Lk32Lk32Lk32@cluster0-9iykb.mongodb.net/test")
  val mongoClient = new MongoClient(uri)
  val database = mongoClient.getDatabase("resume")

}
