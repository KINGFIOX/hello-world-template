# README

## setter 与 getter

感觉 scala 的语法很奇怪

### setter

```scala
class Person {
  private var _age = 0
  // 自定义的getter
  def age: Int = _age
}
```

### getter

```scala
class Person {
  private var _age = 0
  // 自定义的setter
  def age_=(newAge: Int): Unit = {
    if (newAge > 0) _age = newAge
    else println("Age must be positive")
  }
}
```

## 高阶函数

高阶函数是指使用其他函数作为参数、或者返回一个函数作为结果的函数

## case class

scala 编译器 自动的生成一些有用的方法和特性。

```scala
case class WeeklyWeatherForecast(temperatures: Seq[Double]) {

  private def convertCtoF(temp: Double) = temp * 1.8 + 32

  def forecastInFahrenheit: Seq[Double] = temperatures.map(convertCtoF) // <-- passing the method convertCtoF
}
```

下面是展开的结果

```scala
class WeeklyWeatherForecast(val temperatures: Seq[Double]) {

  private def convertCtoF(temp: Double): Double = temp * 1.8 + 32

  def forecastInFahrenheit: Seq[Double] = temperatures.map(convertCtoF)

  // Override equals method
  override def equals(obj: Any): Boolean = obj match {
    case other: WeeklyWeatherForecast => this.temperatures == other.temperatures
    case _                            => false
  }

  // Override hashCode method
  override def hashCode(): Int = temperatures.hashCode()

  // Override toString method
  override def toString: String =
    s"WeeklyWeatherForecast(${temperatures.mkString(",")})"
}
```

可以看到 case class (样例类) 会添加一些东西

1. 给 有参构造添加了 val ， 这个 val 是用来约束为 immutable 的
2. 添加了一些 trait 及其方法: equals, hashCode, toString

### case class -> match && Pattern guards

```scala
abstract class Notification
case class Email(sender: String, title: String, body: String) extends Notification
case class SMS(caller: String, message: String) extends Notification
case class VoiceRecording(contactName: String, link: String) extends Notification
```

应用 case class 的 match

```scala
def showImportantNotification(notification: Notification, importantPeopleInfo: Seq[String]): String = {
  notification match {
    case Email(sender, _, _) if importantPeopleInfo.contains(sender) =>
      "You got an email from special someone!"
    case SMS(number, _) if importantPeopleInfo.contains(number) =>
      "You got an SMS from special someone!"
    // TODO 这里是 模式守卫
    case other =>
      showNotification(other) // nothing special, delegate to our original showNotification function
  }
}

def showNotification(notification: Notification): String = {
  notification match {
    case Email(sender, title, _) =>
      s"You got an email from $sender with title: $title"
    case SMS(number, message) =>
      s"You got an SMS from $number! Message: $message"
    case VoiceRecording(name, link) =>
      s"you received a Voice Recording from $name! Click the link to hear it: $link"
  }
}
```

确实, scala 很有趣，可以解决一些 反射(reflection)的问题

### 仅 模式匹配

```scala
abstract class Device
case class Phone(model: String) extends Device {
  def screenOff = "Turning screen off"
}
case class Computer(model: String) extends Device {
  def screenSaverOn = "Turning screen saver on..."
}

def goIdle(device: Device) = device match {
  case p: Phone => p.screenOff
  case c: Computer => c.screenSaverOn
}
```

### sealed 所有的子类在同一个文件中

sealed 意味着：一个类 ， 所有的子类都必须在同一个文件中

```scala
sealed abstract class Furniture
case class Couch() extends Furniture
case class Chair() extends Furniture

def findPlaceToSit(piece: Furniture): String = piece match {
  case a: Couch => "Lie on the couch"
  case b: Chair => "Sit on the chair"
}
```

## 单例对象 object

C++中，单例模式是：私有化构造函数 + 类内有指针。
这种设计模式有点复杂。 scala 在语言层面上就有 单例模式 object

```scala
// 创建了一个 Email 单例对象
object Email {
  def fromString(emailString: String): Option[Email] = {
    emailString.split('@') match {
      case Array(a, b) => Some(new Email(a, b))
      case _ => None
    }
  }
}
```

### 伴生对象

伴生对象就是：object 与 class 的 标识符相同。
好处就是：单例 和 对象 之间可以相互访问私有成员

```scala
class Email(val username: String, val domainName: String)

object Email {
  def fromString(emailString: String): Option[Email] = {
    emailString.split('@') match {
      case Array(a, b) => Some(new Email(a, b))
      case _ => None
    }
  }
}

val scalaCenterEmail = Email.fromString("scala.center@epfl.ch")
scalaCenterEmail match {
  case Some(email) => println(
    s"""Registered an email
       |Username: ${email.username}
       |Domain name: ${email.domainName}
     """)
  case None => println("Error: could not parse email")
}
```

## for

```scala
case class Student(name: String, score: Int)

val students = List(Student("Alice", 88), Student("Bob", 43), Student("Carol", 91), Student("Dave", 55))

// 使用仅有 for 的循环来打印及格的学生名字
for (student <- students if student.score >= 60) {
  println(student.name)
}
```

### for-yeild

```scala
case class Student(name: String, score: Int)

val students = List(Student("Alice", 88), Student("Bob", 43), Student("Carol", 91), Student("Dave", 55))

val passingStudents = for {
  s <- students if s.score >= 60  // 这里进行了过滤
} yield s.name
```

### for(xxx, xxx) 二维数组

```scala
def foo(n: Int, v: Int) =
   for (i <- 0 until n;
        j <- i until n if i + j == v)
   yield (i, j)

foo(10, 10) foreach {
  case (i, j) =>
    println(s"($i, $j) ")  // prints (1, 9) (2, 8) (3, 7) (4, 6) (5, 5)
}
```

## 提取器对象 + 构造器对象

提取器对象是一个 object + unapply

```scala
class User(val email: String, val name: String)

object Email {
  // 构造器对象
  def apply(username, domain) : String = {
    s"$username--$domain"
  }

  // 提取器方法，尝试从用户的邮箱中提取用户名和域名
  def unapply(email: String): Option[(String, String)] = {
    val parts = email.split("@")
    if (parts.length == 2) Some(parts(0), parts(1)) else None
  }
}

def getEmailInfo(email: String): String = email match {
  // wangfiox@gmail.com 可以直接匹配到 Email.unapply
  case Email(username, domain) => s"Username: $username, Domain: $domain"
  case _ => "No valid email"
}
```

## variant

```scala
class Foo[+A] // A covariant class
class Bar[-A] // A contravariant class
class Baz[A]  // An invariant class
```

### co-variant

我们知道: `List[+A]` ， A 是 co-variant 的

```scala
abstract class Animal {
  def name: String
}
case class Cat(name: String) extends Animal
case class Dog(name: String) extends Animal

object CovarianceTest extends App {
  // 至少一样有用
  def printAnimalNames(animals: List[Animal]): Unit = {
    animals.foreach { animal =>
      println(animal.name)
    }
  }

  val cats: List[Cat] = List(Cat("Whiskers"), Cat("Tom"))
  val dogs: List[Dog] = List(Dog("Fido"), Dog("Rex"))

  printAnimalNames(cats)
  // Whiskers
  // Tom

  printAnimalNames(dogs)
  // Fido
  // Rex
}
```

### contra-variant

```scala
abstract class Printer[-A] {
  def print(value: A): Unit
}
// 特化
class AnimalPrinter extends Printer[Animal] {
  def print(animal: Animal): Unit =
    println("The animal's name is: " + animal.name)
}
class CatPrinter extends Printer[Cat] {
  def print(cat: Cat): Unit =
    println("The cat's name is: " + cat.name)
}

object ContravarianceTest extends App {
  val myCat: Cat = Cat("Boots")

  // 逆变性: 这里按道理来说，只接受 Printer[Cat] 或者是子类
  // 但是这里 Printer[-A] 是 contra-variant , 那么他能接受父类
  def printMyCat(printer: Printer[Cat]): Unit = {
    printer.print(myCat)
  }

  val catPrinter: Printer[Cat] = new CatPrinter
  val animalPrinter: Printer[Animal] = new AnimalPrinter

  printMyCat(catPrinter)
  printMyCat(animalPrinter)
}
```

### in-variant

```scala
class Container[A](value: A) {
  private var _value: A = value
  def getValue: A = _value
  def setValue(value: A): Unit = {
    _value = value
  }
}

val catContainer: Container[Cat] = new Container(Cat("Felix"))
val animalContainer: Container[Animal] = catContainer
animalContainer.setValue(Dog("Spot"))
val cat: Cat = catContainer.getValue // 糟糕，我们最终会将一只狗作为值分配给一只猫
```

还有其他例子，可以混合的

## 类型边界

### 上界

```scala
abstract class Animal {
 def name: String
}

abstract class Pet extends Animal {}

class Cat extends Pet {
  override def name: String = "Cat"
}

class Dog extends Pet {
  override def name: String = "Dog"
}

class Lion extends Animal {
  override def name: String = "Lion"
}

class PetContainer[P <: Pet](p: P) {
  def pet: P = p
}

val dogContainer = new PetContainer[Dog](new Dog)
val catContainer = new PetContainer[Cat](new Cat)

// FIXME this would not compile
// 因为上面是 P <: Pet 而不是 P <: Animal
val lionContainer = new PetContainer[Lion](new Lion)
```

### 下界

```scala
abstract class Animal {
  def name: String
}
class Cat(val name: String) extends Animal {
  def meow(): Unit = println(s"$name says Meow!")
}
class Pussy(val name: String) extends Cat {
  def meow(): Unit = println(s"$name says mi!")
}

class AnimalList[A <: Animal](animals: List[A]) {
  // 应该是，如果 C 是 B 的子类，那么 C 插入到 animalList 的时候，也会有问题
  def add[B >: A](animal: B): AnimalList[B] = new AnimalList(animal :: animals)
}
```

## 抽象类型

```scala
abstract class SeqBuffer extends Buffer {
  type U  // FIXME 像这种，有这个 type 的，就是抽象类型
  type T <: Seq[U]
  def length = element.length
}

// 这里虽然是 extends
abstract class IntSeqBuffer extends SeqBuffer {
  type U = Int
}

def newIntSeqBuf(elem1: Int, elem2: Int): IntSeqBuffer =
  new IntSeqBuffer {  // FIXME 关于抽象类型的实例化
       type T = List[U]
       val element = List(elem1, elem2)
     }

val buf = newIntSeqBuf(7, 8)
println("length = " + buf.length)
println("content = " + buf.element)
```

## 复合类型

```scala
trait Speaker {
  def speak(): Unit
}

trait Reader {
  def read(content: String): Unit
}

// FIXME 复合类型就是将 多个 trait 合成在要一起
def echo(readerSpeaker: Speaker with Reader): Unit = {
  readerSpeaker.speak()
  readerSpeaker.read("Reading some interesting content")
}

class SmartDevice extends Speaker with Reader {
  def speak(): Unit = println("Speaking out loud!")
  def read(content: String): Unit = println(s"Reading: $content")
}

val smartDevice = new SmartDevice()
echo(smartDevice)
```

## 隐式参数

创建了一个 implicit 变量，就会创建一个 隐式上下文，
然后隐式参数就会用到这个 implicit 变量

```scala
abstract class Monoid[A] {
  def add(x: A, y: A): A
  def unit: A
}

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
```

## 隐式转换

```scala
// implicit elem2ordered: A => Ordered[A]
// implicit 表示一个 隐式参数
// elem2ordered: A => Ordered[A] 表示 elem2ordered 是一个函数
// 函数接受 A 返回 Ordered[A]
implicit def list2ordered[A](
    x: List[A]
)(implicit elem2ordered: A => Ordered[A]): Ordered[List[A]] =
  new Ordered[List[A]] {
    // replace with a more useful implementation
    def compare(that: List[A]): Int = 1
  }
```

## 函数式

```scala
object Main extends App {
  def listOfDuplicates[A](x: A, length: Int): List[A] = {
    if (length < 1)
      Nil
    else
      x :: listOfDuplicates(x, length - 1)
  }
  println(listOfDuplicates[Int](3, 4))  // List(3, 3, 3, 3)
  println(listOfDuplicates("La", 8))  // List(La, La, La, La, La, La, La, La)
}
```

## 定义 运算符

```scala
case class Vec(x: Double, y: Double) {
  def +(that: Vec) = Vec(this.x + that.x, this.y + that.y)
}

object Main extends App {
  val vector1 = Vec(1.0, 1.0)
  val vector2 = Vec(2.0, 2.0)

  val vector3 = vector1 + vector2
  vector3.x  // 3.0
  vector3.y  // 3.0
}
```

自定义运算符

```scala
case class MyBool(x: Boolean) {
  def and(that: MyBool): MyBool = if (x) that else this
  def or(that: MyBool): MyBool = if (x) this else that
  def negate: MyBool = MyBool(!x)
}

def not(x: MyBool) = x.negatdef not(x: MyBool) = x.negate
def xor(x: MyBool, y: MyBool) = (x or y) and not(x and y)e
def xor(x: MyBool, y: MyBool) = (x or y) and not(x and y)
```

## 神奇的函数调用方法

```scala
// => Unit 表示 返回 Unit 的表达式，意味着: 我们关心的是执行的副作用 而不是返回值
def whileLoop(condition: => Boolean)(body: => Unit): Unit =
  if (condition) {
    body
    whileLoop(condition)(body)
  }

var i = 2

whileLoop (i > 0) {
  println(i)
  i -= 1
}  // prints 2 1
```

我或许知道了，怎么在 haskell 中使用循环了，感觉这个还是太神奇了

## 注解

### deprecated

```scala
object DeprecationDemo extends App {
  // 编译器将打印 warning "deprecation message"
  @deprecated("deprecation message", "release # which deprecates method")
  def hello = "hola"
  hello
}
```

### tailrec

```scala
import scala.annotation.tailrec

def factorial(x: Int): Int = {

  @tailrec
  def factorialHelper(x: Int, accumulator: Int): Int = {
    if (x == 1) accumulator else factorialHelper(x - 1, accumulator * x)
  }
  factorialHelper(x, 1)  // 确保尾递归
}
```
