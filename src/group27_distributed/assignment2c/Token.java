package group27_distributed.assignment2c;

import java.io.Serializable;
import java.rmi.RemoteException;

public class Token implements Serializable, Sendable {

    public int originID;

    public Token(int originID) {
        this.originID = originID;
    }

    @Override
    public void getSentTo(int senderID, Node_RMI node) {
        try {
            node.recvToken(senderID, this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return "T" + originID;
    }
}
