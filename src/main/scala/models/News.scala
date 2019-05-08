package models

case class News(docId: String, docType: String, headLine: String, dateLine: String, sentences: Seq[String]) {
  override def toString: String = s"$docId:$docType:$headLine:$dateLine:$sentences"
}
