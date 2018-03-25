package resumes.company

import java.util.Date

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import net.liftweb.json.Extraction.decompose
import net.liftweb.json.JsonAST.prettyRender
import net.liftweb.json.parse
import org.bson.Document
import resumes.MongoDB
import resumes.MongoDB.formats
import resumes.company.CompanyManager.Company

object CompanyManager {

  case class Company(
                      name: String,
                      startDate: Date,
                      applications: Int = 0
                    )

}

class CompanyManager(database: MongoDatabase) {

  val COMPANY_COLLECTION = "positions"

  lazy val companies = {
    MongoDB.createCollectionIfNotExists(COMPANY_COLLECTION, database)
    database.getCollection(COMPANY_COLLECTION)
  }

  def addCompany(company: Company) = {
    MongoDB.insertIntoCollection(List(company), companies)
  }

  def getCompany(name: String) = {
    val doc = companies.find(Filters.eq("name", name)).first()
    parse(doc.toJson).extract[Company]
  }

  def replaceCompany(company: Company) = {
    val document = Document.parse(prettyRender(decompose(company)))
    val filter = Filters.eq("name", company.name)
    companies.replaceOne(filter, document)
  }

}
