package resumes.generators.education

import java.util.Calendar

import resumes.generators.Utils
import resumes.generators.education.EducationGenerator.Degree.{Associate, Bachelor, Degree, Master}
import resumes.generators.education.UniversityGenerator.University

import scala.collection.mutable.ListBuffer


object EducationGenerator {

  object Degree extends Enumeration {
    type Degree = Value
    val Associate = Value("Associate")
    val Bachelor = Value("Bachelor")
    val Master = Value("Master")
  }

  object Major extends Enumeration {
    type Major = Value
    val Computer_Science = Value("Computer Science")
    val Design = Value("Design")
    val Information_Technology = Value("Information Technology")
    val Communications = Value("Communications")
    val Political_Science = Value("Political Science")
    val Business = Value("Business administration and management")
    val English_Language_and_Literature = Value("English Language and Literature")
    val Psychology = Value("Psychology")
    val Nursing = Value("Nursing")
    val Chemical_Engineering = Value("Chemical Engineering")
    val Biology = Value("Biology")
    val Engineering = Value("Engineering")
    val Social_sciences_and_history = Value("Social sciences and history")
    val History = Value("History")
    val Accounting = Value("Accounting")
    val Health_Professions = Value("Health Professions")
    val Trades_and_Personal_Services = Value("Trades and Personal Services")
    val Education = Value("Education")
    val Journalism = Value("Journalism")
    val Building_and_Construction = Value("Building and Construction")
    val Mathematics = Value("Mathematics")
  }

//  object Area extends Enumeration {
//    type Area = Value
//    val Sales = Value("Sales")
//  }

  case class Education(
                        startYear: Int,
                        endYear: Int,
                        university: University,
                        degree: String,
                        major: Option[String] = Some(Major.Computer_Science.toString)
                      )

  lazy val highestDegreeGenerator = {
    val distribution = List(
      (Master, 3),
      (Bachelor, 6),
      (Associate, 10)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  lazy val earnedAssociateDegreeSeparatelyGenerator = {
    val distribution = List(
      (true, 2),
      (false, 5)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  lazy val changedUniversityGenerator = {
    val distribution = List(
      (true, 1),
      (false, 8)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  def generateEducation(): List[Education] = {
    var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val education = new ListBuffer[Education]
    val highestDegreeEarned = highestDegreeGenerator.sample()

    val masterUniversity = if (highestDegreeEarned.equals(Master)) {
      val university = UniversityGenerator.generateRandomUniversity()
      education += Education(
        startYear = currentYear - 2,
        endYear = currentYear,
        university = university,
        degree = Master.toString
      )
      currentYear = currentYear - 2
      Some(university)
    } else {
      None
    }

    if (highestDegreeEarned.equals(Master) || highestDegreeEarned.equals(Bachelor)) {
      val bachelorUniversity = if (changedUniversityGenerator.sample() || masterUniversity.isEmpty) {
        UniversityGenerator.generateRandomUniversity()
      } else {
        masterUniversity.get
      }
      val earnedAssociatedSeparately = earnedAssociateDegreeSeparatelyGenerator.sample()
      val yearsInBachelor = if (earnedAssociatedSeparately) 2 else 4
      education += Education(
        startYear = currentYear - yearsInBachelor,
        endYear = currentYear,
        university = bachelorUniversity,
        degree = Bachelor.toString
      )
      if (earnedAssociatedSeparately) {
        currentYear = currentYear - 2
        val associateUniversity = if (changedUniversityGenerator.sample()) {
          UniversityGenerator.generateRandomUniversity()
        } else {
          bachelorUniversity
        }
        education += Education(
          startYear = currentYear - 2,
          endYear = currentYear,
          university = bachelorUniversity,
          degree = Associate.toString
        )
      }
    } else {
      education += Education(
        startYear = currentYear - 2,
        endYear = currentYear,
        university = UniversityGenerator.generateRandomUniversity(),
        degree = Associate.toString
      )
    }
    education.toList
  }

  def main(args: Array[String]): Unit = {
    println("Education generator:")
    (0 to 100).foreach(_ => {
      println("---------------------------")
      val education = generateEducation()
      println(education.mkString("\n"))
    })
  }

}
