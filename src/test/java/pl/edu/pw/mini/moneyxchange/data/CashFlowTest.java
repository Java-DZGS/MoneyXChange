package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import pl.edu.pw.mini.moneyxchange.cashflow.MinCashFlow;
import pl.edu.pw.mini.moneyxchange.utils.Format;
import pl.edu.pw.mini.moneyxchange.utils.splitters.EqualSplitter;
import pl.edu.pw.mini.moneyxchange.utils.splitters.ISplitter;

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

public class CashFlowTest {
    @Test
    public void infinite() {
        int k = 24;

        List<User> users = IntStream.range(0, k)
                .mapToObj(i -> new User(String.valueOf((char) ('a' + i))))
                .toList();

        Money amount = Money.of(10, Format.CURRENCY);
        ISplitter equal = new EqualSplitter(amount);
        users.forEach(user -> equal.addUser(user, ""));

        List<Transfer> transfers = equal.split().entrySet().stream().map(entry -> new Transfer(new Date(), entry.getValue(), entry.getKey(), users.get(0))).toList();

        System.out.println("Transfers: " + transfers);
        long start = System.currentTimeMillis();
        List<Transfer> opt = MinCashFlow.minTransfers(transfers);
        long end = System.currentTimeMillis();
        System.out.println("Optimal: " + opt);
        System.out.println("DEBUG: Logic A took " + (end - start) / 1000.0f + " Seconds");
    }
}
