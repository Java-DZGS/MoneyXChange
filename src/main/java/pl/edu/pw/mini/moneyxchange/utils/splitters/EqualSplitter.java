package pl.edu.pw.mini.moneyxchange.utils.splitters;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EqualSplitter implements ISplitter {
    private final Set<User> includedUsers;
    private final Money expenseAmount;

    public EqualSplitter(Money expenseAmount) {
        this.expenseAmount = expenseAmount;
        includedUsers = new HashSet<>();
    }

    @Override
    public boolean addUser(User user, String text) {
        // no text is expected in equal division
        includedUsers.add(user);
        return true;
    }

    @Override
    public void removeUser(User user) {
        includedUsers.remove(user);
    }

    @Override
    public boolean isReadyToSplit() {
        return !includedUsers.isEmpty();
    }

    @Override
    public Map<User, Money> split() {
        int n = includedUsers.size();
        // todo: handle uneven division like 10 / 3
        Money splitAmount = expenseAmount.divide(n);
        Map<User, Money> outputMap = new HashMap<>();
        for (User user : includedUsers) {
            outputMap.put(user, splitAmount);
        }
        return outputMap;
    }

    @Override
    public String getFeedback() {
        if (includedUsers.isEmpty())
            return "Zaznacz użytkowników biorących udział w wydatku";

        return "Zaznaczeni użytkownicy płacą po " + Format.MONETARY_FORMAT.format(
                expenseAmount.divide(includedUsers.size()));
    }
}