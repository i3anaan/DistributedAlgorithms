package group27_distributed.assignment2c;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class StateCapableNode extends UnicastRemoteObject implements Node_RMI, Runnable{

    private Recording recording;
    private GlobalState globalState;
    public int id;
    Node_RMI[] peers = new Node_RMI[Server.NUMBER_OF_NODES];

    private static final long serialVersionUID = 1L;

    public StateCapableNode(int id) throws RemoteException{
        super();
        this.id = id;
        this.recording = new Recording(this.id);
    }

    @Override
    public void recvMessage(int senderID, Message msg) throws RemoteException {
        System.out.println(this + " Received message: " + msg + " From: " + senderID);

        recording.handleMessageIn(senderID, msg);

        //Now do normal logic:
        this.handleMessageIn(senderID, msg);
    }

    @Override
    public void recvToken(int senderID, Token token) {
        if (!recording.isRecording() && !recording.finishedRecording()) {
            recording.startRecord(this.getState());
            recording.handleToken(senderID, token);
            cascadeToken(token);
        } else {
            recording.handleToken(senderID, token);
        }

        if (recording.finishedRecording()) {
            System.out.println(this + " FINISHED RECORDING");
            try {
                peers[token.originID].recvRecording(this.id, recording);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void recvRecording(int senderID, Recording recording) throws RemoteException {
        System.out.println(this + " Received recording: " + recording + " From: " + senderID);
        globalState.addRecording(recording);
    };

    public void sendMessage(Node_RMI node, Message msg) {
        if (remoteObjectToID(node) == this.id) {
            //Send to self = ignore;
            return;
        }
        System.out.println(this + " Sending message: " + msg + " To: " + this.remoteObjectToID(node));

        try {
            node.recvMessage(this.id, msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //Now do normal logic:
        this.handleMessageOut(msg);
    }

    public GlobalState gatherGlobalState() {
        globalState = new GlobalState(new ArrayList<Recording>());

        recording.startRecord(this.getState());
        cascadeToken(new Token(this.id));

        return globalState;
    }

    public void cascadeToken(Token token) {
        for (int i = 0; i < peers.length; i++) {
            try {
                randomWait();
                peers[i].recvToken(this.id, token);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry();

            for (int i = 0; i < Server.NUMBER_OF_NODES ; i++) {
                Node_RMI peer = (Node_RMI) registry.lookup(""+i);
                this.peers[i] = peer;
            }
            TimeUnit.SECONDS.sleep(1);
            this.startrandombroadcast();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private int remoteObjectToID(Node_RMI node) {
        for (int i = 0; i < peers.length; i++) {
            if (peers[i].equals(node)) {
                return i;
            }
        }
        return -1;
    }

    protected void startrandombroadcast() {
        //To be extended...
    }

    protected void handleMessageIn(int senderID, Message message) {
        //To be extended...
    }

    protected void handleMessageOut(Message message) {
        //To be extended...
    }

    protected State getState() {
        //To be extended...
        return new State();
    }

    protected void randomWait() {
        long sleepiness = ThreadLocalRandom.current().nextLong(100l, 500l);
        try {
            Thread.sleep(sleepiness);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Node[" + this.id + "]";
    }
}
