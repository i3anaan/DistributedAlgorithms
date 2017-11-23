package group27_distributed.assignment2c;

import java.util.concurrent.ArrayBlockingQueue;

public class ChannelRecording extends ArrayBlockingQueue<Message> {
    private boolean tokenSeen = false;

    public ChannelRecording() {
        super(100, true);
    }

    @Override
    public boolean add(Message message) {
        if (message.isToken() || tokenSeen) {
            tokenSeen = true;
            return false;
        }
        return super.add(message);
    }

    public boolean tokenSeen() {
        return tokenSeen;
    }

    public ChannelState getChannelState() {
        return new ChannelState(this);
    }
}
