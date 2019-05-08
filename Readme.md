### event coding

#### tech stack

kafka      
spark   
stanford-corenlp        
mongodb   
petrarch2   

#### pipeline

data -> kafka -> spark RDD -> simhash alogirthm -> stanford-corenlp -> mongo -> create_final_xml -> petrarch

#### execution

sbt clean   
sbt compile   
sbt assembly    
<b>Prodcuer is used to dump initial data into kafka</b>   
java -jar -Dinput=./src/main/input/test target/scala-2.11/deduplication-fat.jar # set main-class as Producer    
<b>OneTime runs the deduplication algorithm + sentence parsing via corenlp</b>    
java -jar -Doffset=3 target/scala-2.11/deduplication-fat.jar # set main-class as OneTime    
<b>MongoToXml reads mongo data into and creates a final xml file</b>
java -jar -Doutput=./src/main/output/result.xml target/scala-2.11/deduplication-fat.jar # set main-class as MongoToXml    
<b>the final xml file is provided as input to petrarch2. its only compatible with python2.7</b>
./src/main/scripts/run_event_coding.sh /anaconda3/lib/python3.6/site-packages/petrarch2/petrarch2.py ./src/main/resources/output/result.xml ./src/main/resources/output/petrarch_result.xml

#### challenges    

you would need atleast 6G of executor memory for parsing the sentences via stanford-corenlp. The spark cluster that I was using had a hard limit of 4G.   

#### to-do list    

read config information from separate conf files.    
run jar with multiple class names rather than separate jar for each.    
create separate spark cluster with max executor memory around 10G.    
