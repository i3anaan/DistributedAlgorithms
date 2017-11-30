package group27_distributed.assignment2c;

import java.io.Serializable;

public class Message implements Serializable{

    public String text;

    public Message(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "$" + text;
    }
}
