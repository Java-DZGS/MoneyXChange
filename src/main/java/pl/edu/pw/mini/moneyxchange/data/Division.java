package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Division {

    public static Map<User, Money> splitEqually(Set<User> users, Money amount) {
        int n = users.size();
        // todo: handle uneven division like 10 / 3
        Money splitAmount = amount.divide(n);
        Map<User, Money> map = new HashMap<>();
        for (User user : users) {
            map.put(user, splitAmount);
        }
        return map;
    }

    public static Map<User, Money> splitExactly(Map<User, Double> userToAmountMap) {
        Map<User, Money> map = new HashMap<>();
        for (var entry : userToAmountMap.entrySet()) {
            User user = entry.getKey();
            Double exactAmount = entry.getValue();

            if (exactAmount != 0)
                map.put(user, Money.of(exactAmount, Format.CURRENCY));

        }

        return map;
    }

    public static Map<User, Money> splitByPercentages(Map<User, Double> userToAmountMap, Money amount) {
        Map<User, Money> map = new HashMap<>();
        for (var entry : userToAmountMap.entrySet()) {
            User user = entry.getKey();
            double percent = entry.getValue();

            if (percent != 0)
                map.put(user, amount.multiply(percent / 100.0));
        }
        return map;
    }

    public static Map<User, Money> splitByShares(Map<User, Double> userToAmountMap, Money amount) {
        // how many total shares
        int n = (int) userToAmountMap.values().stream().mapToInt(Double::intValue).reduce(Integer::sum).orElse(1);
        Map<User, Money> map = new HashMap<>();

        for (var entry : userToAmountMap.entrySet()) {
            User user = entry.getKey();
            int shares = entry.getValue().intValue();

            if (shares != 0)
                map.put(user, amount.multiply(1.0 * shares / n));
        }

        return map;
    }
}
