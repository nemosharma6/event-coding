#### Event Coding

##### Tech Stack

kafka
spark
stanford-corenlp
mongodb
petrarch2

##### pipeline

data is fed into kafka. this data is read as an rdd in spark and a deduplication algorithm, namely simhash, is run on it. each sentence of a document is then processed via stanford-core-nlp to generate parse tree which are then dumped into mongodb.
the data is then picked up from mongodb and processed via petrarch to generate events.

##### execution

sbt clean
sbt compile
sbt assembly
java -jar -Denv=reference -Dinput=./src/main/input/test target/scala-2.11/deduplication-fat.jar # set main-class as Producer
java -jar -Denv=reference -Doffset=3 target/scala-2.11/deduplication-fat.jar # set main-class as OneTime
java -jar -Denv=reference -Doutput=./src/main/output/result.xml target/scala-2.11/deduplication-fat.jar # set main-class as MongoToXml
./src/main/scripts/run_event_coding.sh /anaconda3/lib/python3.6/site-packages/petrarch2/petrarch2.py ./src/main/resources/output/result.xml ./src/main/resources/output/petrarch_result.xml