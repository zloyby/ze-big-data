package by.zloy.storm.storm;

import by.zloy.storm.data.Cdr;
import by.zloy.storm.data.Rater;
import java.util.Map;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class RaterBolt extends BaseBasicBolt {
    private Rater rater;

    public RaterBolt() {
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        rater = new Rater();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        Cdr cdr = (Cdr) tuple.getValue(0);
        cdr.setPrice(rater.calculate(cdr.getClientId(),
                cdr.getCallTime(), cdr.getCallDestination()));
        try {
            Thread.sleep(cdr.getClientId() * 100);
        } catch (InterruptedException e) {
        }
        basicOutputCollector.emit(new Values(cdr));
        System.out.println("RATE>> [" + Thread.currentThread().getId() + "]" + cdr);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("cdr"));
    }
}
