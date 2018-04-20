package resumes.company.position_parser

import resumes.run.Instances

object Run {

  def main(args: Array[String]): Unit = {
    val existingUrls = Instances
      .positionManager
      .getAllPositions()
      .map(_.url)
      .toSet
    val urls = IBMPositionListParser
      .getUrls()
      .filter({case (_, url, _) => !existingUrls.contains(url)})
    val positionParser = new IBMPositionParser
    urls.foreach({ case (area, url, experienceLevel) => {
      val position = positionParser.parsePosition(url, area, experienceLevel)
      Instances.positionManager.uploadPositions(List(position))
    }
    })

  }
}
