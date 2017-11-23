package group27_distributed.assignment2c;

import java.util.HashMap;
import java.util.Map;

public class GlobalState {

    public Map<Integer, State> nodes;
    public Map<Channel, ChannelState> channels;

    public GlobalState(Iterable<Recording> recordings) {
        nodes = new HashMap<>();
        channels = new HashMap<>();

        for (Recording record : recordings) {
            addRecording(record);
        }
    }

    public void addRecording(Recording record) {
        nodes.put(record.nodeID, record.nodeState);

        for (int id  : record.ins.keySet()) {
            channels.put(new Channel(id, record.nodeID), record.ins.get(id).getChannelState());
        }

    }

    public String toString(){
        String out = "";
        for (Map.Entry<Integer, State> pair : nodes.entrySet()) {
            out += "N[" + pair.getKey() + "]: $" + pair.getValue().getTotalMoney() + "\n";
        }
        for (Map.Entry<Channel, ChannelState> pair : channels.entrySet()) {
            out += "E[" + pair.getKey().from + "->" + pair.getKey().to + "]: $" + pair.getValue().getTotalMoney() + "\n";
        }

        return out;
    }
}
