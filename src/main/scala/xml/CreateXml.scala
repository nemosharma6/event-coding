package xml

import models.OptionalMongoEntry

trait CreateXml {

  def generateXml(data: Seq[OptionalMongoEntry], file: String) = {
    val d = data.filter(_.sentences.isDefined)
    val xmlContent =
      <Sentences>
        {d.map { e =>
        // parse date correctly from e.dateLine.get
        val date = "20080804"
        val id = e._id
        val source = "Empty"
        e.sentences.get.map { sen =>
          val senId = id + "_" + sen.sentenceId.substring(sen.sentenceId.length-4)
          <Sentence date={date} id={senId} source={source} sentence="True">
            <Text>
              {sen.sentence}
            </Text>
            <Parse>
              {sen.parseSentence}
            </Parse>
          </Sentence>
        }
      }}
      </Sentences>

    scala.xml.XML.save(file, xmlContent)
  }
}
