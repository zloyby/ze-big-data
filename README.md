===================== Map-reduce test =====================
Helpful URLs:
https://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html#Example%3A+WordCount+v1.0
https://dtflaneur.wordpress.com/2015/10/02/installing-hadoop-on-mac-osx-el-capitan/

1) Start hadoop on macos:
$ hstart

2) Check hadoop:
Resource Manager: http://localhost:50070
JobTracker: http://localhost:8088/
Node Specific Info: http://localhost:8042/

$ jps // Check all services is started
12516 NameNode
12612 DataNode
13096 Jps
12890 ResourceManager
12749 SecondaryNameNode
12991 NodeManager

$ yarn // For resource management more information than the web interface.
$ mapred // Detailed information about jobs

3) Build:
#OLD# $ mkdir ./../target/classes/
#OLD# $ javac -cp $(hadoop classpath) -d ./../target/classes/ ./../src/main/java/by/zloy/MapReduceWordCount.java
#OLD# $ jar -cvf ./hadoop-wordcount.jar -C ./../target/classes/ .

cd ./mapreduce
mvn clean package

4) Run:
# $ hdfs dfs -mkdir /data
# $ hdfs dfs -put ./../data/Pride_and_Prejudice.txt /data/Pride_and_Prejudice.txt
# $ hdfs dfs -cat /data/Pride_and_Prejudice.txt
# $ hdfs dfs -rm -r /user/eugene/output
$ hadoop jar ./target/mapreduce-1.0-SNAPSHOT.jar by.zloy.MapReduceWordCount /data/Pride_and_Prejudice.txt output

5) Check results:
File explorer: http://localhost:50070/explorer.html#/user/eugene/output

===================== Spark test =====================
Helpful URLs:
http://spark.apache.org/docs/latest/quick-start.html

1) Build jar:
$ mvn clean package

2) Run jar:
$ spark-submit --class "by.zloy.SparkTwoLettersCount" --master "local[4]" ./target/spark-1.0-SNAPSHOT.jar

3) See console logs

===================== Storm test =====================
Helpful URLs:
http://www.javahabit.com/2015/12/26/how-to-set-up-apache-storm-on-mac-using-brew/
https://habrahabr.ru/post/186208/

1) Run all deps (zookeeper, nimbus, supervisor and ui):
$ zkServer start
$ /usr/local/opt/storm/libexec/bin/storm nimbus
$ /usr/local/opt/storm/libexec/bin/storm supervisor
$ /usr/local/opt/storm/libexec/bin/storm ui

2) Check that everything is running smoothly:
Storm UI Manager: http://localhost:8772/index.html

$ jps
31937 QuorumPeerMain
32874 nimbus
32764 supervisor
32782 core
32927 Jps

3) Build 2 jars with deps:
$ mvn clean package

4) Run simple and grouped samples:
$ java -jar ./target/storm-simple.jar
$ java -jar ./target/storm-grouped.jar

5) See console logs, like:
CID [139]Cdr{callSource='78119990005', callDestination='1434746452650327933', callTime=32241, clientId=5, price=0}
RATE [119]Cdr{callSource='78119990004', callDestination='7940680539458623485', callTime=19834, clientId=4, price=1586720}
OUT [125]Cdr{callSource='78119990004', callDestination='7940680539458623485', callTime=19834, clientId=4, price=1586720}
