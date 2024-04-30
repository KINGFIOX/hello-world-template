/** @brief
  *   通过 mixin 来合成类
  */

abstract class A {
  val message: String
}
class B extends A {
  val message = "I'm an instance of class B"
}
trait C extends A {
  def loudMessage = message.toUpperCase()
}

// 混入
class D extends B with C

object test2 extends App {
  val point1 = new Point
  point1.x = 99
  point1.y = 101 // prints the warning

  println(point1.x) // 99

  println("Hello, World!")
}
