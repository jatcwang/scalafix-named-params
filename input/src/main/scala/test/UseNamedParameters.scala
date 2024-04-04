/*
rule = UseNamedParameters
 */
package test

case class Two(i: Int, j: Int)
case class Three(i: Int, j: Int, k: Int)
case class Four(i: Int, j: Int, k: Int, l: Int)

object UseNamedParameters {
  Two(1, 2)
  Three(1, 2, 3)
  Four(1, 2, 3, 4)

  new Two(1, 2)
  new Three(1, 2, 3)
  new Four(1, 2, 3, 4)
  val makeFour: (Int, Int) => Four = new Four(1, _, _, 4)

  object Suppresions {
    // scalafix:off
    Two(1, 2)
    Three(1, 2, 3)
    Four(1, 2, 3, 4)

    new Two(1, 2)
    new Three(1, 2, 3)
    new Four(1, 2, 3, 4)
    val makeFour: (Int, Int) => Four = new Four(1, _, _, 4)
    // scalafix:on
  }
}
