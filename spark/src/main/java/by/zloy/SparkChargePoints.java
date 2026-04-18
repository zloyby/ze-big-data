package by.zloy;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import static org.apache.spark.sql.functions.*;

public class SparkChargePoints {

    static final String INPUT = "/home/eugene/Workspace/zloy/ze-big-data/data/electric-chargepoints-2017.csv";
    static final String OUTPUT = "/home/eugene/Workspace/zloy/ze-big-data/data/output/chargepoints-2017-analysis";
    static final String SPARK_PATH = "/home/eugene/Downloads/spark-2.4.6-bin-hadoop2.6";

    final static SparkSession spark = SparkSession.builder()
            .master("local[*]")
            .appName("SparkChargePoints")
            .getOrCreate();

    public static void main(String[] args) {
        String currentPath = System.getenv("PATH");
        System.setProperty("PATH", SPARK_PATH + "/bin:/usr/bin:/bin:/usr/sbin:/sbin:" + currentPath);
        System.setProperty("SPARK_HOME", SPARK_PATH);
        load(transform(extract()));
    }

    static Dataset<Row> extract() {
        return spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(INPUT);
    }

    static Dataset<Row> transform(Dataset<Row> df) {
        String timeFormat = "yyyy-MM-dd HH:mm:ss";
        Dataset<Row> withDuration = df.withColumn("duration_hours",
                unix_timestamp(
                        concat_ws(" ", col("EndDate"), col("EndTime")), timeFormat
                ).minus(
                        unix_timestamp(
                                concat_ws(" ", col("StartDate"), col("StartTime")), timeFormat
                        )).divide(3600.0)
        );

        return withDuration.groupBy(col("CPID").as("chargepoint_id"))
                .agg(
                        avg("duration_hours").as("avg_duration"),
                        max("duration_hours").as("max_duration")
                );
    }

    static void load(Dataset<Row> df) {
        df.write()
                .mode("overwrite")
                .csv(OUTPUT);
    }
}
