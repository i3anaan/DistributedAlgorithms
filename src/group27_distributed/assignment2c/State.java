package group27_distributed.assignment2c;

import java.io.Serializable;

public class State implements Serializable{

    int money; //Simplest example

    public State() {
    }
    public State(int money) {
        this.money = money;
    }

    public int getTotalMoney() {
        return money;
    }
}
