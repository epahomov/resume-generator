package resumes.company

import java.util.Date

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates.inc
import net.liftweb.json.Extraction.decompose
import net.liftweb.json.JsonAST.prettyRender
import net.liftweb.json.parse
import org.bson.Document
import resumes.MongoDB
import resumes.MongoDB.formats
import resumes.company.CompanyManager.Company

object CompanyManager {

  object Companies extends Enumeration {
    type Degree = Value
    val IBM = Value("ibm")
  }

  case class Company(
                      name: String,
                      startDate: Date,
                      applications: Int = 0
                    )

  def main(args: Array[String]): Unit = {
    val companyManager = new CompanyManager(MongoDB.database)
    val company = Company(
      Companies.IBM.toString,
      startDate = new Date()
    )
    companyManager.addCompany(company)
  }

}

class CompanyManager(database: MongoDatabase) {

  val COMPANY_COLLECTION = "companies"

  lazy val companies = {
    MongoDB.createCollectionIfNotExists(COMPANY_COLLECTION, database)
    database.getCollection(COMPANY_COLLECTION)
  }

  def addCompany(company: Company) = {
    MongoDB.insertValueIntoCollection(company, companies)
  }

  def getCompany(name: String) = {
    val doc = companies.find(Filters.eq("name", name)).first()
    parse(doc.toJson).extract[Company]
  }

  def incrementNumberOfApplications(companyName: String): Unit = {
    companies.updateOne(Filters.eq("name", companyName), inc("applications", 1))
  }

  def replaceCompany(company: Company) = {
    val document = Document.parse(prettyRender(decompose(company)))
    val filter = Filters.eq("name", company.name)
    companies.replaceOne(filter, document)
  }

}
