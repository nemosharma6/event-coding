package models

case class MongoEntry(_id: String, hash: String, docType: String, headLine: String, dateLine: String, sentences: Array[ParsedSentence])

object MongoEntry {
  def create(hash: String, news: News, parsedSentence: Array[ParsedSentence]): MongoEntry = {
    MongoEntry(
      news.docId,
      hash,
      news.docType.trim,
      news.headLine.trim,
      news.dateLine.trim,
      parsedSentence
    )
  }
}