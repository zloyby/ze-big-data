package by.zloy;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

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

    static StructType getChargePointsSchema() {
        return new StructType(new StructField[]{
                new StructField("ChargingEvent", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("CPID", DataTypes.StringType, true, Metadata.empty()),
                new StructField("StartDate", DataTypes.StringType, true, Metadata.empty()),
                new StructField("StartTime", DataTypes.StringType, true, Metadata.empty()),
                new StructField("EndDate", DataTypes.StringType, true, Metadata.empty()),
                new StructField("EndTime", DataTypes.StringType, true, Metadata.empty()),
                new StructField("Energy", DataTypes.DoubleType, true, Metadata.empty())
        });
    }

    static Dataset<Row> extract() {
        return spark.read()
                .option("header", "true")
                //.option("inferSchema", "true") // if we want to guess all data types
                .schema(getChargePointsSchema())
                .csv(INPUT);
    }

    static Dataset<Row> transform(Dataset<Row> df) {
        String timeFormat = "yyyy-MM-dd HH:mm:ss";
        Column end = unix_timestamp(concat_ws(" ", col("EndDate"), col("EndTime")), timeFormat);
        Column start = unix_timestamp(concat_ws(" ", col("StartDate"), col("StartTime")), timeFormat);
        Dataset<Row> duration = df.withColumn("duration_hours",
                end.minus(start).divide(3600.0)
        );

        return duration
                .groupBy(col("CPID").as("chargepoint_id"))
                .agg(
                        round(avg("duration_hours"), 2).as("avg_duration"),
                        round(max("duration_hours"), 2).as("max_duration"),
                        round(avg("energy"), 2).as("avg_energy"),
                        round(max("energy"), 2).as("max_energy")
                );
    }

    static void load(Dataset<Row> df) {
        df.coalesce(1) // want to get only one result file
                .write()
                .mode("overwrite")
                .option("header", "true")
                .csv(OUTPUT);
    }
}
