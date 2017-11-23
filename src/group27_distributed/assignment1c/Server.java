package group27_distributed.assignment1c;


import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
 
public class Server {

	static int NUMBER_OF_NODES = 4;
public static void main(String args[]) throws AccessException, RemoteException, AlreadyBoundException {
	
	// I have no fuking clue why we need to actualy use RMI to simulate this stuff.
	Registry registry = LocateRegistry.createRegistry(1099);
	
	List<SimpleNode> nodes = new ArrayList<SimpleNode>();
	
	for (int i = 0 ; i < NUMBER_OF_NODES ; i ++) {
		SimpleNode newNode = new SimpleNode(i);
		registry.bind("" +i, newNode);
		nodes.add(newNode);
	}
	
	for ( SimpleNode node : nodes) {
		new Thread(node).start();
	}
}
}