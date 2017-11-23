package group27_distributed.assignment1c;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node_RMI extends Remote {

	public void recvMessage(int recv_timestamp,int senderID,String msg) throws RemoteException;
	public void recvAck(int messageID) throws RemoteException;
}
