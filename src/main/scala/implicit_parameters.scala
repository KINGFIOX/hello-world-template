abstract class Monoid[A] {
  def add(x: A, y: A): A
  def unit: A
}

import scala.language.implicitConversions

object ImplicitTest {

  implicit val stringMonoid: Monoid[String] = new Monoid[String] {
    def add(x: String, y: String): String = x concat y
    def unit: String = ""
  }

  // 这里创建了一个 隐式上下文
  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    def add(x: Int, y: Int): Int = x + y
    def unit: Int = 0
  }

  // 这里有一个隐式参数 ， 然后他搜索隐式上下文，然后捕获到了这个 stringMonoid 和 intMonoid
  def sum[A](xs: List[A])(implicit m: Monoid[A]): A =
    if (xs.isEmpty) m.unit
    else m.add(xs.head, sum(xs.tail))

  def main(args: Array[String]): Unit = {
    println(sum(List(1, 2, 3))) // uses IntMonoid implicitly
    println(sum(List("a", "b", "c"))) // uses StringMonoid implicitly
  }
}
