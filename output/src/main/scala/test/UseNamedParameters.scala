package test

case class Two(i: Int, j: Int)
case class Three(i: Int, j: Int, k: Int)
case class Four(i: Int, j: Int, k: Int, l: Int)

object UseNamedParameters {
  Two(1, 2)
  Three(i = 1, j = 2, k = 3)
  Four(i = 1, j = 2, k = 3, l = 4)

  new Two(1, 2)
  new Three(i = 1, j = 2, k = 3)
  new Four(i = 1, j = 2, k = 3, l = 4)
  val makeFour: (Int, Int) => Four = new Four(1, _, _, 4)
}
