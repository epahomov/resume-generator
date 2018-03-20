package resumes.generators.name

import resumes.generators.Utils
import resumes.generators.name.FirstNameGenerator.Gender.Gender
import resumes.generators.name.FirstNameGenerator.Origin.Origin

import scala.io.Source

object FirstNameGenerator {

  object Gender extends Enumeration {
    type Gender = Value
    val Male = Value("Male")
    val Female = Value("Female")
  }

  object Origin extends Enumeration {
    type Origin = Value
    val US = Value("US")
    val India = Value("India")
  }

  case class FirstName(name: String,
                       gender: Gender,
                       popularity: Int)

  lazy val (usMaleNamesGenerator, usFemaleNamesGenerator) = {

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

    getGenerator(all)
  }

  private def getGenerator(all: List[FirstName]) = {
    def getGenerator(gender: Gender) = {
      val filtered = all.filter(_.gender.equals(gender))
      Utils.getGeneratorFrequency(filtered.map(x => (x.name, x.popularity)))
    }

    (getGenerator(Gender.Male), getGenerator(Gender.Female))
  }

  lazy val (indiaMaleNamesGenerator, indiaFemaleNamesGenerator) = {

    val rawData = Source
      .fromResource("india_first_names.txt")
      .getLines()
      .toArray

    val all = (0 to 99).flatMap(index => {
      val femaleName = rawData(index * 8 + 3).toLowerCase.capitalize
      val femaleNamePopularity = Math.round(rawData(index * 8 + 2).split(" ")(0).toDouble * 100).toInt
      val maleName = rawData(index * 8 + 7).toLowerCase.capitalize
      val maleNamePopularity = Math.round(rawData(index * 8 + 6).split(" ")(0).toDouble * 100).toInt
      val male = FirstName(maleName, Gender.Male, maleNamePopularity)
      val female = FirstName(femaleName, Gender.Female, femaleNamePopularity)
      List(male, female)
    }).toList

    getGenerator(all)
  }

  def generateRandomFirstName(sex: Gender, origin: Origin) = {
    if (origin.equals(Origin.India)) {
      if (sex.equals(Gender.Male)) {
        indiaMaleNamesGenerator.sample()
      } else {
        indiaFemaleNamesGenerator.sample()
      }
    } else {
      if (sex.equals(Gender.Male)) {
        usMaleNamesGenerator.sample()
      } else {
        usFemaleNamesGenerator.sample()
      }
    }
  }

  def main(args: Array[String]): Unit = {
    println("Male names:")
    for (i <- 1 to 100) {
      println(generateRandomFirstName(Gender.Male, Origin.India))
    }
    println("Female names:")
    for (i <- 1 to 100) {
      println(generateRandomFirstName(Gender.Female, Origin.India))
    }
  }

}
