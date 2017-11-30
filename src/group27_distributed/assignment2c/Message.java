package group27_distributed.assignment2c;

import java.io.Serializable;
import java.rmi.RemoteException;

public class Message implements Serializable, Sendable{

    public String text;

    public Message(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "$" + text;
    }

    @Override
    public void getSentTo(int senderID, Node_RMI node) {
        try {
            node.recvMessage(senderID, this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
