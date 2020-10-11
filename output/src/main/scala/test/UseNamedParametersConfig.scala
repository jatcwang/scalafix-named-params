package test

case class Case(i: Int, s: String)
class Curry(i: Int, s: String)(j: Int, k: Double, l: Long)
case class TParam[A](x1: A, x2: A)
case class TCurry[A](x1: A, x2: A)(y1: A, y2: A)
class Overload(first: Int, `second)`: Long) {
  def this(another: Int, int: Int) = this(first = another, `second)` = int.toLong)
}

object UseNamedParametersConfig {
  def func(i: Int, s: String) = ()

  def `cur)ried`(i: Int, s: String)(j: Int, k: Double, l: Long) = ()

  def overloaded(i: Int, j: Int, k: Int) = ()
  def overloaded(x: Int, y: Int, z: String) = ()

  Case(i = 1, s = "s")
  new Case(i = 2, s = "s")
  new Curry(i = 1, s = "s")(j = 5, k = 6.0, l = 7L)

  func(i = 3, s = "s")
  `cur)ried`(i = 1, s = "s")(j = 7, k = 8.0, l = 9L)
  val partialApply = `cur)ried`(i = 1, s = "s") _
  partialApply(7, 8.0, 9L)

  overloaded(i = 1, j = 2, k = 3)
  overloaded(x = 1, y = 2, z = "3")

  new Overload(first = 1, `second)` = 2L)
  new Overload(another = 1, int = 2)

  TParam[Int](x1 = 1, x2 = 2)
  new TParam[Int](x1 = 3, x2 = 4)

  TCurry[Long](x1 = 1L, x2 = 2L)(3L, 4L)
  new TCurry[Long](x1 = 1L, x2 = 2L)(y1 = 3L, y2 = 4L)
}
