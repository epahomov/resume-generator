package resumes

import net.liftweb.json.Extraction.decompose
import net.liftweb.json.JsonAST.prettyRender
import MongoDB.formats
import org.bson.Document
import net.liftweb.json.parse
import resumes.emails.EmailsManagerUtils.Email

object Utils {

  def toDoc[T](value: T) = {
    val json = valueToJson(value)
    Document.parse(json)
  }

  def fromDoc(document: Document): Email = {
    parse(document.toJson()).extract[Email]
  }

  def valueToJson[T](value: T): String = {
    toJsons(List(value)).head
  }

  def toJsons[T](values: Seq[T]): Seq[String] = {
    values.map(x => {
      prettyRender(decompose(x))
    })
  }

}
