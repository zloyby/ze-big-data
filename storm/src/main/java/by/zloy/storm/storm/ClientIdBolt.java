package by.zloy.storm.storm;

import by.zloy.storm.data.Cdr;
import by.zloy.storm.data.Clients;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class ClientIdBolt extends BaseBasicBolt {
    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        Cdr cdr = (Cdr) tuple.getValue(0);
        cdr.setClientId(Clients.getClientId(cdr.getCallSource()));
        basicOutputCollector.emit(new Values(cdr));
        System.out.println("CID>> [" + Thread.currentThread().getId() + "]" + cdr);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("cdr"));
    }
}
