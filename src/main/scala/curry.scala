object test4 extends App {
  val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
  val res1 = numbers.foldLeft(0)((m, n) => m + n)
  val res2 = numbers.foldLeft(0)(_ + _)

  print(res1) // 55
}
