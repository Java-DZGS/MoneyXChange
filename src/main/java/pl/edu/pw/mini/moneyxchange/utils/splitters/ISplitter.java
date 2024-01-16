package pl.edu.pw.mini.moneyxchange.utils.splitters;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.User;

import java.util.Map;

// proof of concept
public interface ISplitter {
    boolean addUser(User user, String text);
    void removeUser(User user);
    boolean isReadyToSplit();
    Map<User, Money> split();
    String getFeedback();

}

