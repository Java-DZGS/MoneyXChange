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
        Money[] splitAmount = ISplitter.divideAndRemainderCent(expenseAmount, n);
        Map<User, Money> outputMap = new HashMap<>();
        for (User user : includedUsers) {
            outputMap.put(user, splitAmount[0]);
        }

        if(!splitAmount[1].isZero()) {
            //noinspection OptionalGetWithoutIsPresent
            var randomVictim = outputMap.entrySet().stream().skip(ISplitter.random.nextInt(outputMap.entrySet().size())).findFirst().get();
            randomVictim.setValue(randomVictim.getValue().add(splitAmount[1]));
        }

        return outputMap;
    }

    @Override
    public String getFeedback() {
        if (includedUsers.isEmpty())
            return "Zaznacz użytkowników biorących udział w wydatku";

        return "Zaznaczeni użytkownicy płacą po " + Format.MONETARY_FORMAT.format(
                ISplitter.divideAndRemainderCent(expenseAmount, includedUsers.size())[0]);
    }
}