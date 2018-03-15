package resumes.generators.education

import scala.io.Source
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.util.Pair
import scala.collection.JavaConverters._

object UniversityGenerator {

  //1
  //Western Governors University
  //54,735
  //C+
  //  Salt Lake City, UT
  //100%
  //  2
  //Texas A&M University
  //43,531
  //A+
  //  College Station, TX
  //66.64%
  val generator = {

    val rawData = Source
      .fromResource("us_universities.txt")
      .getLines()
      .toArray

    val parsed = (0 to 99).map(index => {
      val universityName: java.lang.String = rawData(index * 6 + 1)
      val universityPopularity = rawData(index * 6 + 2).filter(_.isDigit).toInt
      (universityName, universityPopularity)
    })

    val sumPop = parsed.map(_._2).sum

    val processed = parsed.map({ case (universityName, universityPopularity) => {
      val probability: java.lang.Double = universityPopularity.toDouble / sumPop
      new Pair(universityName, probability)
    }
    }).asJava

    new EnumeratedDistribution(processed)
  }

  def generateRandomUniversity() = {
    generator.sample()
  }

  def main(args: Array[String]): Unit = {
    println("Random universities:")
    (0 to 100).foreach(_ => {
      println(generateRandomUniversity)
    })
  }

}
