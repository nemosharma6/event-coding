package models

case class OptionalMongoEntry(
   _id: String
   , hash: Option[String]
   , docType: Option[String]
   , headLine: Option[String]
   , dateLine: Option[String]
   , sentences: Option[Array[OptionalParsedSentence]])
