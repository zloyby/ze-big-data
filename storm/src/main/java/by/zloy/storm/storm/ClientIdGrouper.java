package by.zloy.storm.storm;

import by.zloy.storm.data.Cdr;
import java.util.ArrayList;
import java.util.List;
import org.apache.storm.generated.GlobalStreamId;
import org.apache.storm.grouping.CustomStreamGrouping;
import org.apache.storm.task.WorkerTopologyContext;

public class ClientIdGrouper implements CustomStreamGrouping {
    private List<Integer> tasks;

    @Override
    public void prepare(WorkerTopologyContext workerTopologyContext, GlobalStreamId globalStreamId,
                        List<Integer> integers) {
        tasks = new ArrayList<>(integers);
    }

    @Override
    public List<Integer> chooseTasks(int i, List<Object> objects) {
        List<Integer> rvalue = new ArrayList<>(objects.size());
        for (Object o : objects) {
            Cdr cdr = (Cdr) o;

            rvalue.add(tasks.get(cdr.getClientId() % tasks.size()));
        }
        return rvalue;
    }
}
