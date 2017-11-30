package group27_distributed.assignment2c;

import java.rmi.RemoteException;
import java.util.concurrent.ThreadLocalRandom;

public class MoneyNode extends StateCapableNode {

    private int money = 0;

    public MoneyNode(int id) throws RemoteException {
        super(id);
    }

    @Override
    protected void startrandombroadcast() {
        //Frantically start spamming everyone with money.

        for ( int i = 0 ; i < 5; i++){
            randomWait();
            Node_RMI destination = peers[ThreadLocalRandom.current().nextInt(0,peers.length)];
            int money = ThreadLocalRandom.current().nextInt(0,20);
            this.sendMessage(destination, new Message(money + ""));
        }
    }

    @Override
    protected void handleMessageIn(int senderID, Message message) {
        changeMoney(Integer.valueOf(message.text));
    }

    @Override
    protected void handleMessageOut(Message message) {
        changeMoney(-1 * Integer.valueOf(message.text));
    }

    private synchronized void changeMoney(int change) {
        this.money -= change;
    }

    @Override
    protected State getState() {
        return new State(this.getMoney());
    }

    public int getMoney() {
        return money;
    }

    @Override
    public String toString() {
        return super.toString() + " $" + money;
    }
}
