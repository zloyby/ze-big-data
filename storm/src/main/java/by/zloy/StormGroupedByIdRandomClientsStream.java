package by.zloy;

import by.zloy.storm.storm.CdrGrouper;
import by.zloy.storm.storm.CdrSpout;
import by.zloy.storm.storm.ClientIdBolt;
import by.zloy.storm.storm.ClientIdGrouper;
import by.zloy.storm.storm.PrintOutBolt;
import by.zloy.storm.storm.RaterBolt;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

public class StormGroupedByIdRandomClientsStream {

    public static void main(String[] args) throws Throwable {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("CdrReader", new CdrSpout());

        builder.setBolt("ClientIdBolt", new ClientIdBolt(), 4).customGrouping("CdrReader", new CdrGrouper());
        builder.setBolt("RaterBolt", new RaterBolt(), 4).customGrouping("ClientIdBolt", new ClientIdGrouper());
        builder.setBolt("PrintOutBolt", new PrintOutBolt(), 2).shuffleGrouping("RaterBolt");

        Config config = new Config();
        config.setDebug(false);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("T1", config, builder.createTopology());
        Thread.sleep(1000 * 10);
        cluster.shutdown();
    }
}
