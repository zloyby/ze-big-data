package by.zloy;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

public class SparkTwoLettersCount {

    static final String INPUT = "/home/eugene/Workspace/zloy/ze-big-data/data/Pride_and_Prejudice.txt";
    static final String SPARK_PATH = "/home/eugene/Downloads/spark-2.4.6-bin-hadoop2.6";

    /**
     * Count words with 'a' and with 'b'
     *
     * @param args empty
     */
    public static void main(String[] args) {
        String currentPath = System.getenv("PATH");
        System.setProperty("PATH", SPARK_PATH + "/bin:/usr/bin:/bin:/usr/sbin:/sbin:" + currentPath);
        System.setProperty("SPARK_HOME", SPARK_PATH);

        SparkConf conf = new SparkConf()
                .setAppName("Simple Application")
                .setMaster("local[*]")
                .set("spark.executor.memory", "1g")
                .set("spark.driver.allowMultipleContexts", "true");

        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
            JavaRDD<String> logData = sc.textFile(INPUT).cache();

            long numAs = logData.filter(new Function<String, Boolean>() {
                public Boolean call(String s) {
                    return s.contains("a");
                }
            }).count();

            long numBs = logData.filter(new Function<String, Boolean>() {
                public Boolean call(String s) {
                    return s.contains("b");
                }
            }).count();

            // Lines with a: 10560, lines with b: 5874
            System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);

            sc.stop();
        }
    }
}
