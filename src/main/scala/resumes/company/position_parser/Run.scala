package resumes.company.position_parser

import resumes.run.Instances

object Run {

  def main(args: Array[String]): Unit = {
    val urls = IBMPositionListParser.getUrls()
    val positionParser = new IBMPositionParser
    val positions = urls.map({ case (area, url) => {
      positionParser.parsePosition(url, area)
    }
    })
    Instances.positionManager.uploadPositions(positions)
  }
}
