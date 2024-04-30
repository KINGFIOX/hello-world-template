object test5 extends App {
  case class Student(name: String, score: Int)

  val students = List(
    Student("Alice", 88),
    Student("Bob", 43),
    Student("Carol", 91),
    Student("Dave", 55)
  )

  val passingStudents: List[String] = for {
    s <- students if s.score >= 60 // 这里进行了过滤
  } yield s.name

  println(passingStudents) // Alice, Carol
}
