package group27_distributed.assignment2c;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class StateCapableNode extends UnicastRemoteObject implements Node_RMI, Runnable{

    private Recording recording;
    private GlobalState globalState;
    public int id;
    Node_RMI[] peers = new Node_RMI[Server.NUMBER_OF_NODES];

    public Map<Integer, Queue<Sendable>> outs = new HashMap<>();
    public Map<Integer, Queue<Sendable>> ins = new HashMap<>();

    private static final long serialVersionUID = 1L;

    public StateCapableNode(int id) throws RemoteException{
        super();
        this.id = id;
        this.recording = new Recording(this.id);

        for (int c = 0; c < Server.NUMBER_OF_NODES; c++) {
            outs.put(c, new ArrayBlockingQueue<Sendable>(100));
            ins.put(c, new ArrayBlockingQueue<Sendable>(100));
        }
    }

    public void recvMessage(int senderID, Message msg) throws RemoteException {
        //System.out.println(this + " Received message: " + msg + " From: " + senderID);

        recording.handleMessageIn(senderID, msg);

        //Now do normal logic:
        this.handleMessageIn(senderID, msg);
    }

    public void recvToken(int senderID, Token token) throws RemoteException {
        System.out.println(this + " Received token: " + token + " From: " + senderID);
        if (!recording.isRecording() && !recording.finishedRecording()) {
            recording.startRecord(this.getState());
            recording.handleToken(senderID, token);
            cascadeToken(token);
        } else {
            recording.handleToken(senderID, token);
        }

        if (recording.finishedRecording()) {
            System.out.println(this + " FINISHED RECORDING");
            peers[token.originID].recvSendable(this.id, recording);
        }
    }

    public void recvRecording(int senderID, Recording recording) throws RemoteException {
        System.out.println(this + " Received recording: " + recording + " From: " + senderID);
        globalState.addRecording(recording);
    };

    public void sendMessage(int receiverID, Message msg) {
        if (receiverID == this.id) {
            //Send to self = ignore;
            return;
        }
        //System.out.println(this + " Sending message: " + msg + " To: " + receiverID);

        //Add to out queue:
        outs.get(receiverID).add(msg);

        //Now do normal logic:
        this.handleMessageOut(msg);
    }

    public GlobalState gatherGlobalState() throws RemoteException {
        globalState = new GlobalState(new ArrayList<Recording>());

        this.recvToken(this.id, new Token(this.id));

        return globalState;
    }

    public void cascadeToken(Token token) {

        for (int p = 0; p < peers.length; p++) {
            outs.get(p).add(token);
            System.out.println(this + " Sent Token to: " + p);
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
            this.busyLoop();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void busyLoop() {
        long until = System.currentTimeMillis() + 10 * 1000;
        while (System.currentTimeMillis() < until) {
            queueSomeMessages();

            //Clear incoming queue
            for (int in = 0; in < Server.NUMBER_OF_NODES; in++) {
                for (Sendable s : ins.get(in)) {
                    s.getSentTo(this.id, this);
                }
            }

            //Instantly clear own send queue, ie. no delay there.
            for (Sendable m : outs.get(this.id)) {
                m.getSentTo(this.id, this);
            }

            //Send some crap to others
            for (int q = 0; q < Server.NUMBER_OF_NODES; q++) {
                Queue<Sendable> queue = outs.get(q);
                Sendable send = queue.peek();
                if (send!=null) {
                    //System.out.println(this + " Queue to: " + q + " - " + Arrays.toString(queue.toArray()));
                    queue.poll().getSentTo(this.id, peers[q]);
                }
            }
            randomWait();
        }
    }

    protected void queueSomeMessages() {
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

    @Override
    public void recvSendable(int senderID, Sendable sendable) {
        ins.get(senderID).add(sendable);
    }
}
