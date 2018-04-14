package resumes.generators.person

import resumes.generators.Utils
import resumes.generators.education.EducationUtils

import scala.io.Source
import scala.util.Random

object SkillsGenerator {

  def normalize(role: String) = {
    EducationUtils
      .normalize(role)
      .replaceAll("junior", "")
      .replaceAll("senior", "")
      .replaceAll("middle", "")
  }

  private def getAllSkillsByRole(role: String): List[String] = {
    try {
      Source
        .fromResource(s"generators/work/role_to_skill/${normalize(role)}.txt")
        .getLines()
        .toList
    } catch {
      case e: Exception => {
        throw new RuntimeException(s"Could not get list of skills for $role", e)
      }
    }
  }

  private lazy val randomSkillsToTakeGenerator = {
    val distribution = List(
      (1, 1),
      (2, 3),
      (3, 5),
      (4, 3),
      (5, 1)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  private lazy val requiredSkillsToTakeGenerator = {
    val distribution = List(
      (0, 1),
      (1, 4),
      (2, 4),
      (3, 1)
    )
    Utils.getGeneratorFrequency(distribution)
  }

  def getSkillsList(role: String,
                    requiredSkills: Option[List[String]],
                    current: Boolean): List[String] = {
    val allSkills = Random.shuffle(getAllSkillsByRole(role))
    val randomSkills = allSkills.take(Math.min(randomSkillsToTakeGenerator.sample(), allSkills.size))
    if (current && requiredSkills.isDefined) {
      val requiredSkillsList = requiredSkills.get
      val skillsToTake = Math.max(Math.min(requiredSkillsList.size, requiredSkillsToTakeGenerator.sample()), requiredSkillsList.size / 4)
      val haveRequiredSkills = requiredSkillsList.take(skillsToTake)
      (haveRequiredSkills ++ randomSkills).toSet.toList
    } else {
      randomSkills.toSet.toList
    }
  }


  private def newLineListFormatter(skills: List[String]) = skills.mkString("\n")

  private def commaListFormatter(skills: List[String]) = skills.mkString(",")

  private def tabListFormatter(skills: List[String]) = skills.mkString("  ")

  private def spaceListFormatter(skills: List[String]) = skills.mkString(" ")

  private val listFormatters: List[(List[String]) => String] = List(newLineListFormatter,
    commaListFormatter,
    tabListFormatter,
    spaceListFormatter
  )

  private def getRandomListFormatter(): (List[String]) => String = {
    Random.shuffle(listFormatters).head
  }

  private def allAppFormatter(skill: String) = skill.toUpperCase()

  private def allDownFormatter(skill: String) = skill.toLowerCase()

  private def capitalizeFormatter(skill: String) = skill.toLowerCase().capitalize

  private val skillFormatters: List[(String) => String] = List(allAppFormatter,
    allDownFormatter,
    capitalizeFormatter
  )

  private def getRandomFormatter(): (String) => String = {
    Random.shuffle(skillFormatters).head
  }

  private def formatSkills(skills: List[String]): String = {
    val skillFormatter = getRandomFormatter()
    getRandomListFormatter()(skills.map(skillFormatter(_)))
  }

  def skillsFomatter(): List[String] => String = {
    formatSkills
  }
}
