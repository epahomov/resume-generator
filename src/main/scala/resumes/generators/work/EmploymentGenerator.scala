package resumes.generators.work

import java.util.Date

object EmploymentGenerator {

  case class Employment(start: Date,
                        end: Date,
                        company: String,
                        description: String,
                        role: String
                       )


}
