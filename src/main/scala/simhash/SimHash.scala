package simhash

trait SimHash {

  def shingles(text: String) = {
    text
      .replaceAll("\\W", "")
      .toLowerCase
      .toCharArray
      .sliding(2)
      .map(_.mkString("").hashCode)
      .toArray
  }

  def isBitSet(num: Int, i: Int) = ((num >> i) & 1) == 1

  def simHash(text: String) = {
    val bitCount = 32

    val table = Array.fill(bitCount)(0)
    val features = shingles(text)

    for (value <- features) {
      for (bit <- Range(0, bitCount)) {
        if (((value >> bit) & 1) == 1) table(bit) += 1
        else table(bit) -= 1
      }
    }

    table.map(e => if (e > 0) 1 else 0)
  }

  def compareHashes(h1: Seq[Int], h2: Seq[Int]): Int = {
    if (h1.isEmpty) return 0
    val rest = compareHashes(h1.tail, h2.tail)
    if (h1.head == h2.head) rest else 1 + rest
  }

}
