package models

case class ParsedSentence(
    sentenceId: String
    , sentence: String
    , parseSentence: String
    , dependencyTree: String
    , token: Array[String]
    , lemma: Array[String]
    , ner: Array[String]
    , corref: Array[String])
