package group27_distributed.assignment2c;

import java.io.Serializable;

public class Token implements Serializable {

    public int originID;

    public Token(int originID) {
        this.originID = originID;
    }
}
