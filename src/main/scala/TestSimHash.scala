import simhash.SimHash

object TestSimHash extends App with SimHash {
  println(simHash("My name is Nimish"))
  println()
  println(simHash("luffy will find one piece"))
  println()
  println(simHash("Nimish is my name"))
}