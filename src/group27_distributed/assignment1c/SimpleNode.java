package group27_distributed.assignment1c;
import java.util.concurrent.ThreadLocalRandom;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class SimpleNode extends UnicastRemoteObject implements Node_RMI, Runnable{

	Clock clock = new Clock();
	int myid;
	
	// Map < MessageID , ( Pending Acks , Message )  
	TreeMap<Integer,Tuple<Integer,String>> undelivered = new TreeMap(); // delivered means send to the local process
	
	List<Node_RMI> peers = new ArrayList();
	private static final long serialVersionUID = 1L;
	
	
	private synchronized Tuple<Integer,String> getWithDefault(int  messageID) {
		
		if (!this.undelivered.containsKey(messageID)) {
			this.undelivered.put(messageID,new Tuple(this.peers.size(),null));
		};
		return this.undelivered.get(messageID) ;
				 
	}
	
	protected SimpleNode(int myID) throws RemoteException {
		super();
		this.myid = myID;
		this.addPeer(this);
	}


	
	
	
	final private void sendMessage(String msg) throws RemoteException {
		int stamp = this.clock.nextStamp();
		for (Node_RMI peer : peers) {
			peer.recvMessage(stamp,this.myid,msg);
		}
	}
	
	final public void recvMessage(int recv_timestamp,int senderID,String msg) throws RemoteException {
		
		int messageID = recv_timestamp*100 + senderID ;// quick hack for total ordering
		this.clock.recvStamp(recv_timestamp);
		
		Tuple entry = this.getWithDefault(messageID);
		entry.y = msg;
		
		this.undelivered.put(messageID,entry);
		
		// send acks
		for (Node_RMI peer : peers) {
			peer.recvAck(messageID);
		}
	}
	
	final synchronized public void recvAck(int messageID) {
		System.out.println("\t\t" + this.myid + ":" + this.clock.stamp + " : ACK : "+messageID);
		Tuple<Integer,String> msgState = this.getWithDefault(messageID);
		msgState.x = msgState.x -1;
		while ( !this.undelivered.isEmpty() && this.undelivered.firstEntry().getValue().x == 0) {
			
			String message = this.undelivered.remove(this.undelivered.firstKey()).y;
			this.processMessage(message);
		}
	}
	
	// Called after from text is ack'ed by all nodes and thus can be delivered
	void processMessage(String msg){
		System.out.println(this.myid + ":" + this.clock.stamp + " : DELIVERED : "+msg);
	}

	// Run as thread, connect to oher nodes,  and send some messages
	public void run() {
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry();
		
		for ( int i = 0 ; i < Server.NUMBER_OF_NODES ; i++) {
			if (i != this.myid ) {
				Node_RMI peer = (Node_RMI) registry.lookup(""+i);
				this.peers.add(peer);
			}
		}
		TimeUnit.SECONDS.sleep(1);
		this.startrandombroadcast();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	final public void addPeer(SimpleNode peer) {
		this.peers.add(peer);
	}
	
	public void startrandombroadcast() throws RemoteException {
		for ( int i = 0 ; i < 3; i++){
			int randomNum = ThreadLocalRandom.current().nextInt(1,5);
			String message = "My id is " + this.myid + " This is my " + i + " th text";
			System.out.println("\t"+ this.myid + " : SENDING : "+message);
			this.sendMessage(message);
		}
	}
	
}