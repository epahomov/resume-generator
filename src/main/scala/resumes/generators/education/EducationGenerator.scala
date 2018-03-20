package resumes.generators.education

import java.util.Calendar

import resumes.generators.Utils
import resumes.generators.education.EducationGenerator.Degree.{Associate, Bachelor, Degree, Master}
import resumes.generators.education.UniversityGenerator.University

import scala.collection.mutable.ListBuffer


object EducationGenerator {

  object Degree extends Enumeration {
    type Degree = Value
    val Associate, Bachelor, Master = Value
  }

  case class Education(
                        startYear: Int,
                        endYear: Int,
                        university: University,
                        degree: Degree
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

    val masterUniversity =  if (highestDegreeEarned.equals(Master)) {
      val university = UniversityGenerator.generateRandomUniversity()
      education += Education(
        startYear = currentYear - 2,
        endYear = currentYear,
        university = university,
        degree = Master
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
        degree = Bachelor
      )
      if (earnedAssociatedSeparately) {
        currentYear = currentYear - 2
        val associateUniversity  = if (changedUniversityGenerator.sample()) {
          UniversityGenerator.generateRandomUniversity()
        } else {
          bachelorUniversity
        }
        education += Education(
          startYear = currentYear - 2,
          endYear = currentYear,
          university = bachelorUniversity,
          degree = Associate
        )
      }
    } else {
      education += Education(
        startYear = currentYear - 2,
        endYear = currentYear,
        university = UniversityGenerator.generateRandomUniversity(),
        degree = Associate
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
