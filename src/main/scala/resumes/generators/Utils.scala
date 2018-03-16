package resumes.generators

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.util.Pair
import scala.collection.JavaConverters._

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

}
