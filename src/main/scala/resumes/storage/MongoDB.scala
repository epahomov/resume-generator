package resumes.storage

import com.mongodb.{MongoClient, MongoClientURI}


object MongoDB {
  private val uri = new MongoClientURI("mongodb+srv://epakhomov:Lk32Lk32Lk32@cluster0-9iykb.mongodb.net/test")
  val mongoClient = new MongoClient(uri)
  val database = mongoClient.getDatabase("resume")
  database.getCollection("emails")
}
