package pl.edu.pw.mini.moneyxchange.utils.splitters;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.money.MonetaryException;
import java.util.HashMap;
import java.util.Map;

public class ExactSplitter implements ISplitter {
    private final Map<User, Money> amounts;
    private final Money expenseAmount;
    private Money parsedAmount;

    public ExactSplitter(Money expenseAmount) {
        this.expenseAmount = expenseAmount;
        amounts = new HashMap<>();
    }

    @Override
    public boolean addUser(User user, String text) {
        if (!parse(text)) {
            amounts.remove(user);
            return false;
        }

        if (!parsedAmount.isZero())
            amounts.put(user, parsedAmount);
        else
            amounts.remove(user);

        return true;
    }

    @Override
    public void removeUser(User user) {
        amounts.remove(user);
    }

    @Override
    public boolean isReadyToSplit() {
        return amounts.values().stream()
                .reduce(Money::add)
                .orElse(Money.zero(expenseAmount.getCurrency()))
                .equals(expenseAmount);
    }

    @Override
    public Map<User, Money> split() {
        Map<User, Money> map = new HashMap<>();
        for (var entry : amounts.entrySet()) {
            User user = entry.getKey();
            Money exactAmount = entry.getValue();

            if (!exactAmount.isZero())
                map.put(user, exactAmount);
        }

        return map;
    }

    @Override
    public String getFeedback() {
        return "Do podzielenia zostało: " +
                Format.MONETARY_FORMAT.format(getRemainder());
    }

    private boolean parse(String text)
    {
        if (text.isEmpty()) {
            parsedAmount = Money.zero(expenseAmount.getCurrency());
            return true;
        }

        try {
            parsedAmount = Money.parse(text, Format.MONETARY_FORMAT);
        } catch (MonetaryException | IllegalArgumentException ex) {
            parsedAmount = Money.zero(expenseAmount.getCurrency());
            return false;
        }

        if (parsedAmount.signum() < 0)
            return false;

        return !parsedAmount.isGreaterThan(expenseAmount);
    }

    private Money getRemainder() {
        return expenseAmount.subtract(
                amounts.values()
                        .stream()
                        .reduce(Money::add)
                        .orElse(Money.zero(expenseAmount.getCurrency())));

    }

}
