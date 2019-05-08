import java.util.Properties

import config.Config
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations

import scala.collection.JavaConversions._
import edu.stanford.nlp.pipeline.{CoreDocument, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree
import edu.stanford.nlp.util.CoreMap

import scala.collection.JavaConverters._

object DependencyParser extends App with Config {

  val sentence = test

  val props = new Properties()
  props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse,coref,kbp,quote,sentiment")
  props.setProperty("coref.algorithm", "neural")

  val pipeline = new StanfordCoreNLP(props)
  val document = new CoreDocument(sentence)
  pipeline.annotate(document)

  val token = document.tokens().get(4)
  println("Token: ", token)

  val sentenceText = document.sentences().get(0)
  println("sentence", sentenceText)

  println("pos tags", document.sentences().get(0).posTags())
  println("ner tags", document.sentences().get(0).nerTags())
  println("constituency parse", document.sentences().get(0).constituencyParse())
  println("dependency parse", document.sentences().get(0).dependencyParse())
  println("lemma", document.tokens().map(e => e.lemma()))
  println("corref", document.corefChains().toString)

  val sentences: List[CoreMap] = document.annotation().get(classOf[CoreAnnotations.SentencesAnnotation]).asScala.toList

  val result = sentences
    .map(sentence => (sentence, sentence.get(classOf[SentimentAnnotatedTree])))
    .foldLeft(0.0, 0) {
      case ((acc1, acc2), (sen, tree)) =>
        val s = RNNCoreAnnotations.getPredictedClass(tree).toDouble
        (acc1 + sen.toString.length * s, acc2 + sen.toString.length)
    }

  val weightedResult = result._1 / result._2
}
