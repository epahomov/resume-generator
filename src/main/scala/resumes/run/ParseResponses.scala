package resumes.run

import resumes.run.Instances.responseParser

object ParseResponses {
  def main(args: Array[String]): Unit = {
    responseParser.parse()
  }
}
