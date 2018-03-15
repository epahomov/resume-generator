package resumes.generators.name

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.util.Pair
import resumes.generators.name.FirstNameGenerator.Gender.Gender

import scala.collection.JavaConverters._
import scala.io.Source

object FirstNameGenerator {

  object Gender extends Enumeration {
    type Gender = Value
    val Male, Female = Value
  }

  case class FirstName(name: String,
                       gender: Gender,
                       popularity: Int)

  lazy val (maleNamesGenerator, femaleNamesGenerator) = {

    val all = Source
      .fromResource("us_first_names.txt")
      .getLines()
      .toList
      .tail
      .flatMap(line => {
        // 1	Jacob	273,746	Emily	223,640
        // 2	Michael	250,471	Madison	193,112
        val lineValues = line.split("\t")
        val maleName = lineValues(1)
        val maleNamePopularity = lineValues(2).filter(_.isDigit).toInt
        val femaleName = lineValues(3)
        val femaleNamePopularity = lineValues(4).filter(_.isDigit).toInt
        val male = FirstName(maleName, Gender.Male, maleNamePopularity)
        val female = FirstName(femaleName, Gender.Female, femaleNamePopularity)
        List(male, female)
      })

    def getGenerator(gender: Gender) = {
      val filtered = all.filter(_.gender.equals(gender))
      val popSum = filtered.map(_.popularity).sum
      val processed = filtered.map(firstName => {
        val value: java.lang.String = firstName.name
        val probability: java.lang.Double = firstName.popularity.toDouble / popSum
        new Pair(value, probability)
      }).asJava
      new EnumeratedDistribution(processed)
    }

    (getGenerator(Gender.Male), getGenerator(Gender.Female))
  }

  def generateRandomFirstName(sex: Gender) = {
    if (sex.equals(Gender.Male)) {
      maleNamesGenerator.sample()
    } else {
      femaleNamesGenerator.sample()
    }
  }

  def main(args: Array[String]): Unit = {
    println("Male names:")
    for (i <- 1 to 100) {
      println(generateRandomFirstName(Gender.Male))
    }
    println("Female names:")
    for (i <- 1 to 100) {
      println(generateRandomFirstName(Gender.Female))
    }
  }

}
