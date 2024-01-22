package pl.edu.pw.mini.moneyxchange.utils.splitters;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.User;

import java.util.Map;
import java.util.Random;

// proof of concept
public interface ISplitter {
    Random random = new Random();

    boolean addUser(User user, String text);
    void removeUser(User user);
    boolean isReadyToSplit();
    Map<User, Money> split();
    String getFeedback();

    static Money[] divideAndRemainderCent(Money money, long divisor) {
        Money[] result = money.multiply(100).divideAndRemainder(divisor);
        return new Money[]{result[0].divide(100), result[1].divide(100)};
    }
}

