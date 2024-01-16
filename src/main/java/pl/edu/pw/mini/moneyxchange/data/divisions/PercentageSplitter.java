package pl.edu.pw.mini.moneyxchange.data.divisions;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.User;

import java.util.HashMap;
import java.util.Map;

public class PercentageSplitter implements ISplitter {
    private Map<User, Double> percentages;
    private final Money expenseAmount;
    private double parsedAmount;

    public PercentageSplitter(Money expenseAmount) {
        this.expenseAmount = expenseAmount;
        percentages = new HashMap<>();
    }

    @Override
    public boolean addUser(User user, String text) {
        if (!parse(text)) {
            percentages.remove(user);
            return false;
        }

        if (parsedAmount != 0)
            percentages.put(user, parsedAmount);
        else
            percentages.remove(user);

        return true;
    }

    @Override
    public void removeUser(User user) {
        percentages.remove(user);
    }

    @Override
    public Map<User, Money> split() {
        Map<User, Money> map = new HashMap<>();
        for (var entry : percentages.entrySet()) {
            User user = entry.getKey();
            double percent = entry.getValue();

            if (percent != 0)
                map.put(user, expenseAmount.multiply(percent / 100.0));
        }
        return map;
    }

    private boolean parse(String text) {
        if (text.isEmpty()) {
            parsedAmount = 0.0;
            return true;
        }

        try {
            parsedAmount = Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            parsedAmount = 0.0;
            return false;
        }

        if (parsedAmount < 0 || parsedAmount > 100)
            return false;

        return true;
    }
}
