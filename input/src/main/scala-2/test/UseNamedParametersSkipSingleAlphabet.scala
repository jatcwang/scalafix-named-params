/*
rule = UseNamedParameters
UseNamedParameters.minParams = 1
UseNamedParameters.skipSingleAlphabet = true
 */
package test

object UseNamedParametersSkipSingleAlphabet {
  def func(i: Int, s: String) = ()

  def `cur)ried`(i: Int, s: String)(j: Int, k: Double, l: Long) = ()

  def overloaded(i: Int, j: Int, k: Int) = ()
  def overloaded(x: Int, y: Int, z: String) = ()

  Case(1, "s")
  new Case(2, "s")
  Case.apply(3, "z")
  new Curry(1, "s")(5, 6.0, 7L)

  func(3, "s")
  `cur)ried`(1, "s")(7, 8.0, 9L)
  val partialApply = `cur)ried`(1, "s") _
  partialApply(7, 8.0, 9L)

  overloaded(1, 2, 3)
  overloaded(1, 2, "3")

  new Overload(1, 2L)
  new Overload(1, 2).method(1, 2)

  TParam[Int](1, 2)
  new TParam[Int](3, 4)

  TCurry[Long](1L, 2L)(3L, 4L)
  new TCurry[Long](1L, 2L)(3L, 4L)

  VarArgs("a", 1, 2, 3).varArgs("b", 4, 5, 6)

  new JavaClass(1, "s").method(2, "ss")

  Map.apply("a" -> 1, "b" -> 2)

  object Suppressions {
    // scalafix:off
    new Overload(1, 2L)
    new Overload(1, 2).method(1, 2)
    // scalafix:on
  }
}
