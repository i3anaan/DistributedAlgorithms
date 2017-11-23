package group27_distributed.assignment2c;

import java.io.Serializable;
import java.util.*;

public class Recording implements Serializable{

    private boolean recorded = false;
    private boolean recording = false;

    public State nodeState;
    public int nodeID;
    //public Map<Integer, ChannelRecording> outs = new HashMap<Integer, ChannelRecording>();
    public Map<Integer, ChannelRecording> ins = new HashMap<Integer, ChannelRecording>();

    public Recording(int id) {
        this.nodeID = id;
    }

    public void startRecord(State state){
        if (!recorded && !recording) {
            recording = true;
            nodeState = state;
        }
    }

    public Recording stopRecord(){
        if (!recording) {
            throw new UnsupportedOperationException();
        }
        recorded = true;
        recording = false;
        return this;
    }

    public boolean handleMessageIn(int senderID, Message message) {
        if (!recording) {
            return false;
        }
        //Add to queue
        if (ins.get(senderID) == null) {
            ins.put(senderID, new ChannelRecording());
        }

        ins.get(senderID).add(message);

        if (ins.values().stream().allMatch(c -> c.tokenSeen())) {
            //Seen tokens on all incoming channels
            stopRecord();
        }

        return hasRecorded();
    }

    public void handleMessageOut(Message message) {
        if (!recording) {
            return;
        }
    }

    public boolean isRecording() {
        return recording;
    }

    public boolean hasRecorded() {
        return !recording && recorded;
    }
}
