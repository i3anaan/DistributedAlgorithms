package group27_distributed.assignment2c;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

public class Recording implements Serializable, Sendable {
    private boolean recording = false;

    public State nodeState;
    public int nodeID;
    //public Map<Integer, ChannelRecording> outs = new HashMap<Integer, ChannelRecording>();
    public Map<Integer, ChannelRecording> ins = new HashMap<Integer, ChannelRecording>();

    public Recording(int id) {
        this.nodeID = id;
        for (int i = 0; i < Server.NUMBER_OF_NODES; i++) {
            ins.put(i, new ChannelRecording());
        }
    }

    public void startRecord(State state){
        if (!finishedRecording() && !recording) {
            System.out.println(this + " - RECORDING");
            recording = true;
            nodeState = state;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void handleToken(int senderID, Token token){
        ins.get(senderID).addToken(token);
        System.out.println(this + " Recorded channel (" + senderID + "): " + Arrays.toString(ins.get(senderID).toArray()));
    }

    public void handleMessageIn(int senderID, Message message) {
        if (!recording) {
            return;
        }

        ins.get(senderID).add(message);
    }

    public boolean isRecording() {
        return recording;
    }

    public boolean finishedRecording() {
        //All incoming recordings have seen the token.
        return ins.values().stream().allMatch(c -> c.tokenSeen());
    }

    public String toString() {
        return "Recording[" + nodeID + "]";
    }

    @Override
    public void getSentTo(int senderID, Node_RMI node) {
        try {
            node.recvRecording(senderID, this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
