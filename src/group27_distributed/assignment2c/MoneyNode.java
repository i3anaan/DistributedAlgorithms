package group27_distributed.assignment2c;

import java.rmi.RemoteException;
import java.util.concurrent.ThreadLocalRandom;

public class MoneyNode extends StateCapableNode {

    private int money = 0;

    public MoneyNode(int id) throws RemoteException {
        super(id);
    }

    @Override
    protected void queueSomeMessages() {
        //For each outgoing channel
        for (int c = 0; c < Server.NUMBER_OF_NODES; c++) {
            if (this.id != c) { //Do not sent to self.
                //Send 0 or 1 messages
                int messageCount = ThreadLocalRandom.current().nextInt(0, 2);
                for (int m = 0; m < messageCount; m++) {
                    int money = ThreadLocalRandom.current().nextInt(0, 20);
                    sendMessage(c, new Message(money + ""));
                }
            }

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
