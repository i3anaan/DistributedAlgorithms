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
            Node_RMI destination = peers[ThreadLocalRandom.current().nextInt(0,peers.length)];
            int money = ThreadLocalRandom.current().nextInt(0,20);
            long sleepiness = ThreadLocalRandom.current().nextLong(0l, 100l);
            this.sendMessage(destination, Message.getMoneyMessage(money));

            try {
                Thread.sleep(sleepiness);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void handleMessageIn(int senderID, Message message) {
        if (!message.isToken()) {
            this.money += Integer.valueOf(message.text);
        }
    }

    @Override
    protected void handleMessageOut(Message message) {
        if (!message.isToken()) {
            this.money -= Integer.valueOf(message.text);
        }
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
