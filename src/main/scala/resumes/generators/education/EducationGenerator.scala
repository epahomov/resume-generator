package resumes.generators.education

import java.util.Calendar

import resumes.generators.Utils
import resumes.company.PositionManager.Area.Area
import resumes.company.PositionManager.{Area, Position}
import resumes.generators.education.Enums.Degree.{Associate, Bachelor, Master}
import resumes.generators.education.UniversityGenerator.University
import EducationUtils._
import scala.collection.mutable.ListBuffer
import Enums._

object EducationGenerator {



  case class Education(
                        startYear: Int,
                        endYear: Int,
                        university: University,
                        degree: String,
                        major: Option[String] = Some(Major.Computer_Science.toString),
                        GPA: Option[Double] = Some(gpaGenerator.sample())
                      )
  import Enums.Major._

  lazy val gpaGenerator = {
    val distribution = List(
      (4.0, 1),
      (3.9, 2),
      (3.8, 3),
      (3.7, 4),
      (3.6, 10),
      (3.5, 6),
      (3.4, 4),
      (3.3, 3),
      (3.2, 2)
    )
    Utils.getGeneratorFrequency(distribution)
  }

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
      case Area.PR => PRAreaMajorGenerator.sample()
    }
  }

  def main(args: Array[String]): Unit = {
    println("Education generator:")
    (0 to 100).foreach(_ => {
      println("---------------------------")
      val position = Position(null, null, area = Some(Area.PR))
      val education = generateEducation(position)
      println(education.mkString("\n"))
    })
  }

}
