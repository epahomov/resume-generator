package resumes.response.result.identifiers

import resumes.response.result.SimpleResultIdentifier

object SalesForceResultIdentifier extends SimpleResultIdentifier {
  val ACCEPTED_STRINGS: List[String] = List(
    "Talk About You at Salesforce"
  )
  val DECLINED_STRINGS: List[String] = List(
    "fortunate to have a strong group of applicants"
  )

}