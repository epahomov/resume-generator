package resumes.generators.education

import java.util.Calendar

import resumes.generators.Utils
import resumes.company.PositionManager.Area.Area
import resumes.company.PositionManager.{Area, Position}
import resumes.generators.education.EducationGenerator.Degree.{Associate, Bachelor, Master}
import resumes.generators.education.UniversityGenerator.University
import EducationUtils._
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
    val Graphic_Design = Value("Graphic Design")
    val Interior_Design = Value("Interior Design")
    val Fashion_Design = Value("Fashion Design")
    val Industrial_Design  = Value("Industrial Design")
    val Art  = Value("Art")
    val Fine_Arts  = Value("Fine Arts")
    val Media_Production  = Value("Media Production")
    val Landscape_Architecture  = Value("Landscape Architecture")
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
    val Computer_Engineering_Technology = Value("Computer Engineering and Technology")
    val Computer_Engineering = Value("Computer Engineering")
    val Social_sciences_and_history = Value("Social sciences and history")
    val History = Value("History")
    val Accounting = Value("Accounting")
    val Health_Professions = Value("Health Professions")
    val Trades_and_Personal_Services = Value("Trades and Personal Services")
    val Education = Value("Education")
    val Journalism = Value("Journalism")
    val Building_and_Construction = Value("Building and Construction")
    val Mathematics = Value("Mathematics")
    val Finance = Value("Finance")
    val Economics = Value("Economics")
  }

  case class Education(
                        startYear: Int,
                        endYear: Int,
                        university: University,
                        degree: String,
                        major: Option[String] = Some(Major.Computer_Science.toString)
                      )
  import EducationGenerator.Major._

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

  def generateEducation(position: Position): List[Education] = {
    var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val education = new ListBuffer[Education]
    val highestDegreeEarned = highestDegreeGenerator.sample()
    val requiredMajor = position.requiredMajor.getOrElse(getMajorByArea(position.area.get))
    val masterUniversity = if (highestDegreeEarned.equals(Master)) {
      val university = UniversityGenerator.generateRandomUniversity()
      education += Education(
        startYear = currentYear - 2,
        endYear = currentYear,
        university = university,
        degree = Master.toString,
        major = Some(requiredMajor.toString)
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
      val bachelorMajor = if (highestDegreeEarned.equals(Bachelor)) requiredMajor else generateMajor(position, requiredMajor)
      education += Education(
        startYear = currentYear - yearsInBachelor,
        endYear = currentYear,
        university = bachelorUniversity,
        degree = Bachelor.toString,
        major = Some(bachelorMajor.toString)
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
          degree = Associate.toString,
          major = Some(bachelorMajor.toString)
        )
      }
    } else {
      education += Education(
        startYear = currentYear - 2,
        endYear = currentYear,
        university = UniversityGenerator.generateRandomUniversity(),
        degree = Associate.toString,
        major = Some(requiredMajor.toString)
      )
    }
    education.toList
  }

  def generateMajor(position: Position, requiredMajor: Major): Major = {
    if (sameMajorGenerator.sample()) {
      requiredMajor
    } else {
      if (closeMajor.sample()) {
        getMajorByArea(position.area.getOrElse(MajorToArea.get(requiredMajor).get))
      } else {
        totallyRandomMajorGenerator.sample()
      }
    }
  }

  lazy val sameMajorGenerator = {
    val distribution = List(
      (true, 5),
      (false, 1)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  lazy val closeMajor = {
    val distribution = List(
      (true, 5),
      (false, 1)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  def getMajorByArea(area: Area): Major = {
    area match {
      case Area.Computer_Science => computerScienceAreaMajorGenerator.sample()
      case Area.Hardware => hardwareAreaMajorGenerator.sample()
      case Area.Design => designAreaMajorGenerator.sample()
      case Area.Finance => financeAreaMajorGenerator.sample()
    }
  }

  def main(args: Array[String]): Unit = {
    println("Education generator:")
    (0 to 100).foreach(_ => {
      println("---------------------------")
      val position = Position(null, null, area = Some(Area.Finance))
      val education = generateEducation(position)
      println(education.mkString("\n"))
    })
  }

}
