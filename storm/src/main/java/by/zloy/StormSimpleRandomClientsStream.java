package by.zloy;

import by.zloy.storm.storm.CdrSpout;
import by.zloy.storm.storm.PrintOutBolt;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

public class StormSimpleRandomClientsStream {

    public static void main(String[] args) throws Throwable {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("CdrReader", new CdrSpout());
        builder.setBolt("PrintOutBolt", new PrintOutBolt(), 2).shuffleGrouping("CdrReader");

        Config config = new Config();
        config.setDebug(false);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("T1", config, builder.createTopology());
        Thread.sleep(1000 * 10);
        cluster.shutdown();
    }
}
