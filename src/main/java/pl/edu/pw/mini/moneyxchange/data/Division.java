package pl.edu.pw.mini.moneyxchange.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Division {

    public static HashMap<User, Double> splitEqually(HashSet<User> users, double amount) {
        int n = users.size();
        // todo: handle uneven division like 10 / 3
        double splitAmount = amount / n;
        HashMap<User, Double> map = new HashMap<>();
        for (User user : users) {
            map.put(user, splitAmount);
        }
        return map;
    }

    public static HashMap<User, Double> splitExactly(HashMap<User, Double> userToAmountMap) {
        HashMap<User, Double> map = new HashMap<>();
        for (Map.Entry<User, Double> entry : userToAmountMap.entrySet()) {
            User user = entry.getKey();
            double exactAmount = entry.getValue();

            if (exactAmount != 0)
                map.put(user, exactAmount);

        }

        return map;
    }

    public static HashMap<User, Double> splitByPercentages(HashMap<User, Double> userToAmountMap, double amount) {
        HashMap<User, Double> map = new HashMap<>();
        for (Map.Entry<User, Double> entry : userToAmountMap.entrySet()) {
            User user = entry.getKey();
            double percent = entry.getValue();

            map.put(user, (percent / 100) * amount);
        }
        return map;
    }

    public static HashMap<User, Double> splitByShares(HashMap<User, Double> userToAmountMap, double amount) {
        // how many total shares
        int n = (int) userToAmountMap.values().stream().mapToInt(Double::intValue).reduce(Integer::sum).orElse(1);
        HashMap<User, Double> map = new HashMap<>();

        for (Map.Entry<User, Double> entry : userToAmountMap.entrySet()) {
            User user = entry.getKey();
            int shares = entry.getValue().intValue();

            map.put(user, (1.0 * shares / n) * amount);
        }

        return map;
    }
}
