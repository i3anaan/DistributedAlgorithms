package group27_distributed.assignment2c;

import java.util.concurrent.ArrayBlockingQueue;

public class ChannelRecording extends ArrayBlockingQueue<Message> {
    private Token tokenSeen = null;

    public ChannelRecording() {
        super(100, true);
    }

    @Override
    public boolean add(Message message) {
        if (tokenSeen()) {
            //Dont record this channel if it already received the token.
            return false;
        }
        return super.add(message);
    }

    public void addToken(Token token) {
        this.tokenSeen = token;
    }

    public boolean tokenSeen() {
        return tokenSeen != null;
    }

    public ChannelState getChannelState() {
        return new ChannelState(this);
    }
}
