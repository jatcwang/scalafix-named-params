/*
rule = UseNamedParameters
UseNamedParameters.minParams = 1
 */
package test

case class Case(i: Int, s: String)
class Curry(i: Int, s: String)(j: Int, k: Double, l: Long)
case class TParam[A](x1: A, x2: A)
case class TCurry[A](x1: A, x2: A)(y1: A, y2: A)
class Overload(first: Int, `second)`: Long) {
  def this(another: Int, int: Int) = this(another, int.toLong)
  def method(first: Int, second: Int): Unit = ()
}
case class VarArgs(a: String, i: Int*) {
  def varArgs(b: String, i: Int*): Unit = ()
}

object UseNamedParametersConfig {
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

  VarArgs(a = "a", i = 1, 2, 3).varArgs("b", 4, 5, 6)

  new JavaClass(1, "s").method(2, "ss")

  List(1, 2, 3).map { _.toLong }.map { case _ => 1 }
  Map.apply("a" -> 1, "b" -> 2)
}
