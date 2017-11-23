package group27_distributed.assignment2c;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Server {

	public static int NUMBER_OF_NODES = 4;

	public static void main(String args[]) throws AccessException, RemoteException, AlreadyBoundException, InterruptedException {

		// I have no fuking clue why we need to actualy use RMI to simulate this stuff.
		Registry registry = LocateRegistry.createRegistry(1099);

		List<StateCapableNode> nodes = new ArrayList<StateCapableNode>();

		for (int i = 0 ; i < NUMBER_OF_NODES ; i ++) {
			StateCapableNode newNode = new MoneyNode(i);
			registry.bind("" +i, newNode);
			nodes.add(newNode);
		}

		for ( StateCapableNode node : nodes) {
			new Thread(node).start();
		}


		Thread.sleep(2000);

		long seed = 120391203l;
		Random rand = new Random(seed);
		StateCapableNode theChosenOne = nodes.get(rand.nextInt(NUMBER_OF_NODES));

		System.out.println("TheChosenOne: " + theChosenOne);

		GlobalState state = theChosenOne.gatherGlobalState();

		Thread.sleep(1000l);
		System.out.println(state);
	}
}