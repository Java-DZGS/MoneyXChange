package pl.edu.pw.mini.moneyxchange.data.divisions;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.User;

import java.util.Map;

// proof of concept
public interface ISplitter {
    boolean addUser(User user, String text);
    void removeUser(User user);
    Map<User, Money> split();

}

