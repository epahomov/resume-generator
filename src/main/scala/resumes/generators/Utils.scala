package resumes.generators

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.util.Pair
import resumes.company.PositionManager.Area

import scala.collection.JavaConverters._
import scala.io.Source

object Utils {

  def getGeneratorFrequency[T](data: scala.collection.immutable.Seq[(T, Int)]): EnumeratedDistribution[T] = {
    val total = data.map(_._2).sum
    val normalized = data.map({ case (v, frequency) => {
      (v, frequency.toDouble / total)
    }
    })
    getGenerator(normalized)
  }

  def getGenerator[T](data: scala.collection.immutable.Seq[(T, Double)]): EnumeratedDistribution[T] = {
    val processed = data.map({ case (v, probability) => {
      val probab: java.lang.Double = probability
      new Pair(v, probab)
    }
    }).asJava
    new EnumeratedDistribution(processed)
  }


  val DEFAULT_WEIGHT = 10

  def generatorFromFile(path: String): EnumeratedDistribution[String] = {
    val data = Source
      .fromResource(path)
      .getLines()
      .map(line => {
        val pair = line.split(",")
        val value = pair(0)
        val weight = if (pair.size > 1) pair(1).toInt else DEFAULT_WEIGHT
        (value, weight)
      }).toList
    Utils.getGeneratorFrequency(data)
  }

  def trueFalseDistribution(forTrue: Int, forFalse: Int): EnumeratedDistribution[Boolean] = {
    {
      val distribution = List(
        (true, forTrue),
        (false, forFalse)
      )
      Utils.getGeneratorFrequency(distribution)
    }
  }


  val areaToFileSystemName = {
    Map(
      Area.Computer_Science -> "computer_science",
      Area.Hardware -> "hardware",
      Area.Design -> "design",
      Area.PR -> "pr",
      Area.Finance -> "finance"
    )
  }

}
