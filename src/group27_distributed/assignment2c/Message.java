package group27_distributed.assignment2c;

import java.io.Serializable;

public class Message implements Serializable{

    private boolean isToken;
    public String text;

    private Message(boolean token, String text) {
        this.isToken = token;
        this.text = text;
    }

    public boolean isToken(){
        return isToken;
    }

    public int getTokenOrigin() {
        if (!isToken()) {
            throw new UnsupportedOperationException();
        }

        else return Integer.valueOf(text);
    }

    public static Message getToken(int chosenOne) {
        return new Message(true, chosenOne + "");
    }

    public static Message getMoneyMessage(int money) {
        return new Message (false, money + "");
    }

    @Override
    public String toString() {
        return text;
    }
}
