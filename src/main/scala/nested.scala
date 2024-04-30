/** @brief
  *   使用嵌套
  */
object test3 extends App {

  /** @brief
    *   将递归函数封装起来
    *
    * @param x
    * @return
    */
  def factorial(x: Int): Int = {
    def fact(x: Int, accumulator: Int): Int = {
      if (x <= 1) accumulator
      else fact(x - 1, x * accumulator)
    }
    fact(x, 1)
  }

  println("Factorial of 2: " + factorial(2))
  println("Factorial of 3: " + factorial(3))
}
