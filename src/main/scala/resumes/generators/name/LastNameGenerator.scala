package resumes.generators.name

import resumes.generators.Utils

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
        val probability = values(1).take(values(1).size - 1).toDouble / 100
        (values(0), probability)
      })
    Utils.getGenerator(data)

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
