package by.zloy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MapReduceWordCount extends Configured implements Tool {

    private static int printUsage() {
        System.out.println("MapReduceWordCount [-m #mappers ] [-r #reducers] input_file output_file");
        ToolRunner.printGenericCommandUsage(System.out);
        return -1;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new MapReduceWordCount(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {

        JobConf conf = new JobConf(getConf(), MapReduceWordCount.class);
        conf.setJobName("MapReduceWordCount");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(MapClass.class);
        conf.setCombinerClass(Reduce.class);
        conf.setReducerClass(Reduce.class);

        List<String> other_args = new ArrayList<>();
        for (int i = 0; i < args.length; ++i) {
            try {
                if ("-m".equals(args[i])) {
                    conf.setNumMapTasks(Integer.parseInt(args[++i]));
                } else if ("-r".equals(args[i])) {
                    conf.setNumReduceTasks(Integer.parseInt(args[++i]));
                } else {
                    other_args.add(args[i]);
                }
            } catch (NumberFormatException except) {
                System.out.println("ERROR: Integer expected instead of " + args[i]);
                return printUsage();
            } catch (ArrayIndexOutOfBoundsException except) {
                System.out.println("ERROR: Required parameter missing from " + args[i - 1]);
                return printUsage();
            }
        }

        // Make sure there are exactly 2 parameters left.
        if (other_args.size() != 2) {
            System.out.println("ERROR: Wrong number of parameters: " + other_args.size() + " instead of 2.");
            return printUsage();
        }

        FileInputFormat.setInputPaths(conf, other_args.get(0));
        FileOutputFormat.setOutputPath(conf, new Path(other_args.get(1)));

        JobClient.runJob(conf);
        return 0;
    }

    // Mapper Class, Counts words in each line. For each line, break the line into words and emits them as (word, 1)
    private static class MapClass extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            String line = value.toString();
            StringTokenizer itr = new StringTokenizer(line);
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                output.collect(word, one);
            }
        }
    }

    // Reducer class that just emits the sum of the input values.
    private static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }
            output.collect(key, new IntWritable(sum));
        }
    }
}