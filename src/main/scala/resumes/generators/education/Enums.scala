package resumes.generators.education

object Enums {

  object Degree extends Enumeration {
    type Degree = Value
    val Associate = Value("Associate")
    val Bachelor = Value("Bachelor")
    val Master = Value("Master")
  }

  type Major = String

}
