package models

case class OptionalParsedSentence(
    sentenceId: String
    , sentence: String
    , parseSentence: String
    , dependencyTree: String
    , token: Array[String]
    , lemma: Array[String]
    , ner: Array[String]
    , corref: Option[Array[String]])