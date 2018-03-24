package resumes.generators.person

import scala.util.Random

object PhoneNumberGenerator {

  // https://gist.github.com/joeyv/7087747
  def generateRandomNumber(): String = {
    val num1 = Random.nextInt(7) + 1
    val num2 = Random.nextInt(8)
    val num3 = Random.nextInt(8)
    val set2 = Random.nextInt(643) + 100
    val set3 = Random.nextInt(8999) + 1000
    "" + num1 + num2 + num3 + set2 + set3
  }

  def main(args: Array[String]): Unit = {
    println("Phone number generator:")
    (0 to 100).foreach(_ => {
      println(generateRandomNumber())
    })
  }
}
