package resumes.generators.name

import resumes.generators.Utils
import resumes.generators.name.FirstNameGenerator.Origin
import resumes.generators.name.FirstNameGenerator.Origin.Origin

import scala.io.Source

object LastNameGenerator {

  lazy val indiaGenerator = {
    val data = Source
      .fromResource("generators/names/india_last_names.txt")
      .getLines()
      .toList
      .map(_.split(" ").flatMap(_.split("\t")).filter(_.size > 0))
      .map(parsed => {
          val lastName = parsed(1)
          val popularity = parsed(2).filter(_.isDigit).toInt
        (lastName, popularity)
      })
    Utils.getGeneratorFrequency(data)
  }

  lazy val usGenerator = {
    //  1.	  Smith	1.01%
    //  2.	  Johnson	0.81%
    //  3.	  Williams	0.70%
    //  4.	  Jones	0.62%
    val data = Source
      .fromResource("generators/names/us_last_names.txt")
      .getLines()
      .toList
      .map(_.split(" ")(2).split("\t"))
      .map(values => {
        val probability = values(1).take(values(1).size - 1).toDouble / 100
        (values(0), probability)
      })
    Utils.getGenerator(data)

  }

  def generateLastName(origin: Origin) = {
    if (Origin.India.equals(origin)) {
      indiaGenerator.sample()
    } else {
      usGenerator.sample()
    }
  }

  def main(args: Array[String]): Unit = {
    println("Surnames")
    for (i <- 1 to 100) {
      println(generateLastName(Origin.India))
    }
  }

}
