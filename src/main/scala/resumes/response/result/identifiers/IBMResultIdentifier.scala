package resumes.response.result.identifiers

import resumes.response.result.SimpleResultIdentifier

object IBMResultIdentifier extends SimpleResultIdentifier {
  val ACCEPTED_STRINGS: List[String] = List(
    "for the next step in the IBM interview process",
    "a cognitive ability assessment",
    "send me a copy",
    "a complete resume",
    "potential match",
    "detailed Resume",
    "resubmit your resume"
  )
  val DECLINED_STRINGS: List[String] = List(
    "pursuing other candidates"
  )

}
