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
      .filter({case (_, url) => !existingUrls.contains(url)})
    val positionParser = new IBMPositionParser
    val positions = urls.map({ case (area, url) => {
      positionParser.parsePosition(url, area)
    }
    })
    Instances.positionManager.uploadPositions(positions)
  }
}
