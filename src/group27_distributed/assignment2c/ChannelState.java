package group27_distributed.assignment2c;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ChannelState extends State{

    public Queue<Message> messages;

    public ChannelState(Queue<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getTotalMoney() {
        int total = 0;
        for (Message message : messages) {
            total += Integer.valueOf(message.text);
        }
        this.money = total;
        return super.getTotalMoney();
    }
}
