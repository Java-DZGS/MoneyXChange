package pl.edu.pw.mini.moneyxchange.utils.splitters;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import java.util.HashMap;
import java.util.Map;

public class SharesSplitter implements ISplitter {
    private final Map<User, Integer> shares;
    private final Money expenseAmount;

    private int parsedAmount;

    public SharesSplitter(Money expenseAmount) {
        this.expenseAmount = expenseAmount;
        shares = new HashMap<>();
    }

    @Override
    public boolean addUser(User user, String text) {
        if (!parse(text)) {
            shares.remove(user);
            return false;
        }

        if (parsedAmount != 0)
            shares.put(user, parsedAmount);
        else
            shares.remove(user);

        return true;
    }

    @Override
    public void removeUser(User user) {
        shares.remove(user);
    }

    @Override
    public boolean isReadyToSplit() {
        return !shares.isEmpty();
    }

    @Override
    public Map<User, Money> split() {
        // how many total shares
        int n = (int) shares.values().stream().reduce(Integer::sum).orElse(1);
        Map<User, Money> map = new HashMap<>();

        for (var entry : shares.entrySet()) {
            User user = entry.getKey();
            int shares = entry.getValue();

            if (shares != 0)
                map.put(user, expenseAmount.multiply(1.0 * shares / n));
        }

        return map;
    }

    @Override
    public String getFeedback() {
        if (shares.isEmpty())
            return "Wpisz udziały użykowników";

        int n = (int) shares.values().stream().reduce(Integer::sum).orElse(1);
        // todo: wyrzuca wyjątek gdy wynik dzielenia jest ułamkiem z nieskończonym rozwinięciem dziesiętnym XD
        return "Jeden udział wynosi " + Format.MONETARY_FORMAT.format(expenseAmount.divide(n));
    }

    private boolean parse(String text) {
        if (text.isEmpty()) {
            parsedAmount = 0;
            return true;
        }

        // todo: check how parsing works with inputting double values (exc or rounding)
        try {
            parsedAmount = Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            parsedAmount = 0;
            return false;
        }

        if (parsedAmount < 0)
            return false;

        return true;
    }
}
