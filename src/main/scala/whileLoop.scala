object test6 extends App {
  def whileLoop(condition: => Boolean)(body: => Unit): Unit =
    if (condition) {
      body
      whileLoop(condition)(body)
    }

  var i = 7

  whileLoop(i > 0) {
    println(i)
    i -= 1
  } // prints 2 1
}
