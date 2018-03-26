My tests with Spark, Storm and Hadoop MapReduce
============

## Map-reduce test

Helpful FAQs:
[1](https://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html#Example%3A+WordCount+v1.0)
[2](https://dtflaneur.wordpress.com/2015/10/02/installing-hadoop-on-mac-osx-el-capitan/)

### Start and stop hadoop on macos:
~~~ bash
$ /usr/local/Cellar/hadoop/2.7.3/sbin/start-dfs.sh;/usr/local/Cellar/hadoop/2.7.3/sbin/start-yarn.sh
$ /usr/local/Cellar/hadoop/2.7.3/sbin/stop-yarn.sh;/usr/local/Cellar/hadoop/2.7.3/sbin/stop-dfs.sh
~~~

### Check hadoop:
Check local URLs of
[Resource Manager](http://localhost:50070) ,
[JobTracker](http://localhost:8088/) ,
[Node Specific Info](http://localhost:8042/)

~~~ bash
$ jps // Check all services is started
12516 NameNode
12612 DataNode
13096 Jps
12890 ResourceManager
12749 SecondaryNameNode
12991 NodeManager
~~~
~~~ bash
$ yarn // For resource management more information than the web interface
$ mapred // Detailed information about jobs
~~~

### Build:
~~~ bash
$ mkdir ./../target/classes/
$ javac -cp $(hadoop classpath) -d ./../target/classes/ ./../src/main/java/by/zloy/MapReduceWordCount.java
$ jar -cvf ./hadoop-wordcount.jar -C ./../target/classes/ .
~~~
Or by maven:
~~~ bash
$ mvn clean package
~~~

### Run:
~~~ bash
$ hdfs dfs -mkdir /data
$ hdfs dfs -put ./../data/Pride_and_Prejudice.txt /data/Pride_and_Prejudice.txt
$ hdfs dfs -cat /data/Pride_and_Prejudice.txt
$ hdfs dfs -rm -r /user/eugene/output
$ hadoop jar ./target/mapreduce-1.0-SNAPSHOT.jar by.zloy.MapReduceWordCount /data/Pride_and_Prejudice.txt output
~~~

### Check results:
Check local URL of [File explorer](http://localhost:50070/explorer.html#/user/eugene/output)

# Spark test

Helpful FAQs:
[1](http://spark.apache.org/docs/latest/quick-start.html)

### Build jar:
~~~ bash
$ mvn clean package
~~~

### Run jar:
~~~ bash
$ spark-submit --class "by.zloy.SparkTwoLettersCount" --master "local[4]" ./target/spark-1.0-SNAPSHOT.jar
~~~

### See console logs, like:
~~~ bash
Lines with a: 10560, lines with b: 5874
~~~

# Storm test

Helpful FAQs:
[1](http://www.javahabit.com/2015/12/26/how-to-set-up-apache-storm-on-mac-using-brew/)
[2](https://habrahabr.ru/post/186208/)

### Run all deps (zookeeper, nimbus, supervisor and ui):
~~~ bash
$ zkServer start
$ /usr/local/opt/storm/libexec/bin/storm nimbus
$ /usr/local/opt/storm/libexec/bin/storm supervisor
$ /usr/local/opt/storm/libexec/bin/storm ui
~~~

### Check that everything is running smoothly:
Check local URL of [Storm UI Manager](http://localhost:8772/index.html)

~~~ bash
$ jps
31937 QuorumPeerMain
32874 nimbus
32764 supervisor
32782 core
32927 Jps
~~~

### Build 2 jars with deps:
~~~ bash
$ mvn clean package
~~~

### Run simple and grouped samples:
~~~ bash
$ java -jar ./target/storm-simple.jar
$ java -jar ./target/storm-grouped.jar
~~~

### See console logs, like:
~~~ bash
CID [139]Cdr{callSource='78119990005', callDestination='1434746452650327933', callTime=32241, clientId=5, price=0}
RATE [119]Cdr{callSource='78119990004', callDestination='7940680539458623485', callTime=19834, clientId=4, price=1586720}
OUT [125]Cdr{callSource='78119990004', callDestination='7940680539458623485', callTime=19834, clientId=4, price=1586720}
~~~