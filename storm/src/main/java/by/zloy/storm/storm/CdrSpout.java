package by.zloy.storm.storm;

import by.zloy.storm.data.Cdr;
import by.zloy.storm.data.Clients;
import java.util.Map;
import java.util.Random;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class CdrSpout extends BaseRichSpout {
    private static final String NUMBERS[] = Clients.getClients().keySet().toArray(new String[0]);
    private int count;
    private SpoutOutputCollector collector;
    private Random random;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("cdr"));
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        random = new Random();
    }

    @Override
    public void nextTuple() {
        try {
            Thread.sleep(100);
            if (++count > 20) {
                return;
            }
        } catch (InterruptedException ignored) {
        }
        collector.emit(new Values(generate()));
    }

    private Cdr generate() {
        return new Cdr(
                NUMBERS[random.nextInt(NUMBERS.length)],
                Long.toString(Math.abs(random.nextLong())),
                random.nextInt(32768));
    }
}
