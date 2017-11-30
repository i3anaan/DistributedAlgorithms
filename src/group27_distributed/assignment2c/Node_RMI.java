package group27_distributed.assignment2c;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node_RMI extends Remote {

	public void recvMessage(int senderID, Message message) throws RemoteException;
	public void recvToken(int senderID, Token token) throws RemoteException;
	public void recvRecording(int senderID, Recording recording) throws RemoteException;
}
