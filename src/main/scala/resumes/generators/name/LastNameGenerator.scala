package resumes.generators.name

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.util.Pair

import scala.collection.JavaConverters._
import scala.io.Source

object LastNameGenerator {

  lazy val generator = {
    //  1.	  Smith	1.01%
    //  2.	  Johnson	0.81%
    //  3.	  Williams	0.70%
    //  4.	  Jones	0.62%
    val data = Source
      .fromResource("us_last_names.txt")
      .getLines()
      .toList
      .map(_.split(" ")(2).split("\t"))
      .map(values => {
        val value: java.lang.String = values(0)
        val probability: java.lang.Double = values(1).take(values(1).size - 1).toDouble / 100
        new Pair(value, probability)
      }).asJava
    new EnumeratedDistribution(data)
  }

  def generateLastName() = {
    generator.sample()
  }

  def main(args: Array[String]): Unit = {
    println("Surnames")
    for (i <- 1 to 100) {
      println(generateLastName)
    }
  }

}
