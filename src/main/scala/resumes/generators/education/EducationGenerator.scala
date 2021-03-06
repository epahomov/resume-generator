package resumes.generators.education

import java.util.Calendar

import resumes.company.PositionManager.{Area, Position}
import resumes.generators.Utils
import resumes.generators.education.EducationUtils._
import resumes.generators.education.Enums.Degree.{Associate, Bachelor, Degree, Master}
import resumes.generators.education.Enums._
import resumes.generators.education.UniversityGenerator.University
import resumes.generators.person.PersonGenerator.Comment

import scala.collection.mutable.ListBuffer
import scala.util.Random

object EducationGenerator {


  case class Education(
                        startYear: Int,
                        endYear: Int,
                        university: University,
                        degree: String,
                        major: Option[String] = Some("Computer Science"),
                        GPA: Option[Double] = Some(gpaGenerator.sample())
                      )

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

  lazy val earnedAssociateDegreeSeparatelyGenerator = Utils.trueFalseDistribution(forTrue = 2, forFalse = 5)
  lazy val changedUniversityGenerator = Utils.trueFalseDistribution(forTrue = 1, forFalse = 8)
  lazy val exactHighestDegree = Utils.trueFalseDistribution(forTrue = 3, forFalse = 1)
  lazy val sameMajorGenerator = Utils.trueFalseDistribution(forTrue = 5, forFalse = 1)
  lazy val closeMajor = Utils.trueFalseDistribution(forTrue = 5, forFalse = 1)

  def getHighestDegreeEarned(position: Position): (Degree, Comment) = {
    val someDegree = highestDegreeGenerator.sample()
    position.minimumDegreeNecessary.map(Degree.withName(_)) match {
      case Some(degree) => {
        if (exactHighestDegree.sample()) {
          (degree, "Used exact minimum degree")
        } else {
          degree match {
            case Degree.Associate => (Degree.Bachelor, "Upgraded final degree")
            case Degree.Bachelor => if (Random.nextBoolean()) {
              (Degree.Master, "Upgraded final degree")
            } else {
              (Degree.Associate, "Downgraded final degree")
            }
            case Degree.Master => (Degree.Bachelor, "Downgraded final degree")
          }
        }
      }
      case None => {
        (someDegree, "Minimum degree wasn't specified, used random one")
      }
    }
  }
  def generateEducation(position: Position, yearEnd: Int = Calendar.getInstance().get(Calendar.YEAR)): (List[Education], Comment) = {
    var currentYear = yearEnd
    val education = new ListBuffer[Education]
    val (highestDegreeEarned, comment) = getHighestDegreeEarned(position)
    val requiredMajor = position.requiredMajor.getOrElse(getRandomMajorByArea(Area.withName(position.area.get)))
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
          university = associateUniversity,
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
    (education.toList, comment)
  }

  def generateMajor(position: Position, requiredMajor: Major): Major = {
    if (sameMajorGenerator.sample()) {
      requiredMajor
    } else {
      if (closeMajor.sample()) {
        getRandomMajorByArea(position.area.map(Area.withName(_)).getOrElse(getAreaByMajor(requiredMajor)))
      } else {
        getRandomMajor()
      }
    }
  }



}
