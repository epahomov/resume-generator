package resumes

import net.liftweb.json.Extraction.decompose
import net.liftweb.json.JsonAST.prettyRender
import org.bson.Document
import resumes.MongoDB.formats

object Utils {

  def toDoc[T](value: T) = {
    val json = valueToJson(value)
    Document.parse(json)
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
