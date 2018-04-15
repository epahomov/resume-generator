package resumes.generators.person

import resumes.generators.Utils

import scala.io.Source
import scala.util.Random

object PhoneNumberGenerator {

  lazy val stateToCode = {
    Source
      .fromResource("generators/codes.txt")
      .getLines()
      .map(line => {
        val code = line.split("\t")(0)
        val state = line.split("\t")(1)
        (code, state)
      })
      .filter(_._2 != "--")
      .toList
      .groupBy(_._2)
      .map({ case (state, pairs) => {
        val data = pairs.map({ case (code, state) => {
          (code, 1)
        }
        })
        state -> Utils.getGeneratorFrequency(data)
      }
      })
  }

  // https://gist.github.com/joeyv/7087747
  def generateRandomNumber(stateShortName: String): String = {
    val code = stateToCode.get(stateShortName).get.sample()
    val set2 = Random.nextInt(643) + 100
    val set3 = Random.nextInt(8999) + 1000
    code + set2 + set3
  }

}
