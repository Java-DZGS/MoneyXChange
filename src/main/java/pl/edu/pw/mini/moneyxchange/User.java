package pl.edu.pw.mini.moneyxchange;

import java.io.Serializable;

// TODO
public class User implements Serializable {

    String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
