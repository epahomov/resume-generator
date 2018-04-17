package resumes.generators.education

import org.apache.commons.math3.distribution.EnumeratedDistribution
import resumes.company.PositionManager.Area
import resumes.company.PositionManager.Area.Area
import resumes.generators.Utils
import resumes.generators.education.Enums.Major

import scala.io.Source

object EducationUtils {

  private lazy val areaToGenerator: Map[Area, EnumeratedDistribution[Major]] = {
    Area
      .values
      .map(area => {
        val fileName = Utils.areaToFileSystemName.get(area).get
        val generator = Utils.generatorFromFile(s"generators/education/area_to_major/$fileName.txt")
        area -> generator
      }).toMap
  }



  private lazy val normalizesAreaToArea: Map[String, Area] = {
    Area
      .values
      .map(area => {
        resumes.Utils.normalize(area.toString) -> area
      }).toMap
  }

  private lazy val majorToArea: Map[Major, Area] = {
    Source
      .fromResource("generators/education/major_to_area.txt")
      .getLines()
      .map(line => {
        val pair = line.split(",")
        val major = pair(0)
        val area = pair(1)
        resumes.Utils.normalize(major) -> normalizesAreaToArea.get(resumes.Utils.normalize(area)).get
      }).toMap
  }

  lazy val randomAreaGenerator: EnumeratedDistribution[Area] = {
    val data = Area
      .values
      .map(area => {
        (area, 1)
      }).toList
    Utils.getGeneratorFrequency(data)
  }

  def getRandomMajorByArea(area: Area.Value): Major = {
    areaToGenerator.get(area).get.sample()
  }

  def getAreaByMajor(major: Major): Area.Value = {
    majorToArea.get(resumes.Utils.normalize(major)).get
  }

  def getRandomMajor(): Major = {
    getRandomMajorByArea(randomAreaGenerator.sample())
  }

}
