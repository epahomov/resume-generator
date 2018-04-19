package resumes.generators.name

import org.apache.commons.math3.distribution.EnumeratedDistribution
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
    val Arab = Value("Arab")
    val China = Value("China")
  }

  case class FirstName(name: String,
                       gender: Gender,
                       popularity: Int)

  private lazy val (usMaleNamesGenerator, usFemaleNamesGenerator) = {

    val all = Source
      .fromResource("generators/names/us_first_names.txt")
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

  private lazy val (indiaMaleNamesGenerator, indiaFemaleNamesGenerator) = {

    val rawData = Source
      .fromResource("generators/names/india_first_names.txt")
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

  private lazy val (chineseMaleNamesGenerator, chineseFemaleNamesGenerator) = {
    val rawData = Source
      .fromResource("generators/names/chinese_first_names.txt")
      .getLines()
      .map(line => {
        val splitted = line.split("\t")
        val gender = if (splitted(0).equals("F")) FirstNameGenerator.Gender.Female else FirstNameGenerator.Gender.Male
        val name = splitted(1)
        (gender, name)
      }).toList

    def getGenerator(gender: Gender): EnumeratedDistribution[String] = {
      val data = rawData
        .filter(_._1.equals(gender))
        .map({ case (_, name) => (name, 1) })
      Utils.getGeneratorFrequency(data)
    }

    (getGenerator(Gender.Male), getGenerator(Gender.Female))
  }

  private lazy val (arabMaleNamesGenerator, arabFemaleNamesGenerator) = {
    def getGenerator(file: String) = {
      val data = Source
        .fromResource(s"generators/names/arab_${file}_first_names.txt")
        .getLines()
        .toList
      Utils.getSimpleGenerator(data)
    }
    (getGenerator("male"), getGenerator("female"))
  }

  def generateRandomFirstName(sex: Gender, origin: Origin) = {
    origin match {
      case Origin.India => {
        sex match {
          case Gender.Male => indiaMaleNamesGenerator.sample()
          case Gender.Female => indiaFemaleNamesGenerator.sample()
        }
      }
      case Origin.US => {
        sex match {
          case Gender.Male => usMaleNamesGenerator.sample()
          case Gender.Female => usFemaleNamesGenerator.sample()
        }
      }
      case Origin.China => {
        sex match {
          case Gender.Male => chineseMaleNamesGenerator.sample()
          case Gender.Female => chineseFemaleNamesGenerator.sample()
        }
      }
      case Origin.Arab => {
        sex match {
          case Gender.Male => arabMaleNamesGenerator.sample()
          case Gender.Female => arabFemaleNamesGenerator.sample()
        }
      }
    }
  }

}
