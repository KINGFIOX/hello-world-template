class Point {
  private var _x = 0
  private var _y = 0
  private val bound = 100

  /** @brief
    *   定义了 getter ，用于获取 _x 的值
    *
    * @return
    */
  def x = _x

  /** @brief
    *   定义了 setter ，用于设置 _x 的值
    *
    * @return
    */
  def x_=(newValue: Int): Unit = {
    if (newValue < bound) _x = newValue else printWarning
  }

  def y = _y

  def y_=(newValue: Int): Unit = {
    if (newValue < bound) _y = newValue else printWarning
  }

  private def printWarning = println("WARNING: Out of bounds")
}

object test1 extends App {
  val point1 = new Point
  point1.x = 99
  point1.y = 101 // prints the warning

  println(point1.x) // 99

  println("Hello, World!")

}
