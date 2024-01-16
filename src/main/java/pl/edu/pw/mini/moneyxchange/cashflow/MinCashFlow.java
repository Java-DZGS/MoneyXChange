package pl.edu.pw.mini.moneyxchange.cashflow;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.Transfer;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class MinCashFlow {
    public static List<Transfer> minTransfers(List<Transfer> transfers) {
        // The array 'balance' will hold the net amount for up to 12 individuals
        // Negative values mean the person needs to pay that amount, positive values mean the person should receive that amount
        int max = transfers.stream()
                .flatMapToInt(transfer -> IntStream.of(transfer.getFromUser().getId(), transfer.getToUser().getId()))
                .max()
                .orElse(0);
        double[] balance = new double[max + 1];
        User[] userIds = new User[max + 1];
        int userCount = 0;

        // Calculate the balance for each person involved in the transactions
        for (var transfer : transfers) {
            balance[transfer.getFromUser().getId()] += transfer.getAmount().getNumber().doubleValue();
            balance[transfer.getToUser().getId()] -= transfer.getAmount().getNumber().doubleValue();
            userIds[transfer.getFromUser().getId()] = transfer.getFromUser();
            userIds[transfer.getToUser().getId()] = transfer.getToUser();
        }

        // Create a list to store non-zero balances (amounts that need to be settled)
        int index = 0;
        List<Double> nonZeroBalances = new ArrayList<>();
        List<User> users = new ArrayList<>();
        for (double b : balance) {
            if (b != 0) {
                nonZeroBalances.add(b);
                User user = userIds[index];
                users.add(user);
            }
            index++;
        }


        // Prepare to find the minimum number of transactions to settle all debts
        int numAccounts = nonZeroBalances.size();
        int[] minTransfers = new int[1 << numAccounts]; // 1<<numAccounts is 2^numAccounts
        Arrays.fill(minTransfers, Integer.MAX_VALUE / 2); // Initialize with a large value
        minTransfers[0] = 0; // No transfers needed when there is no debt

        int[] prevSubset = new int[1 << numAccounts];

        // Loop through all possible subsets of debts
        for (int i = 1; i < (1 << numAccounts); ++i) {
            int sum = 0;

            // Calculate the sum of balances in the current subset
            for (int j = 0; j < numAccounts; ++j) {
                if ((i >> j & 1) == 1) { // If the j-th person is in the current subset (i)
                    sum += nonZeroBalances.get(j);
                }
            }

            // If the sum is zero, then the current subset can be settled among themselves
            if (sum == 0) {
                // Set initial transfers for this subset as the number of involved accounts minus 1 transfer
                minTransfers[i] = Integer.bitCount(i) - 1;

                // Try to split the subset into two parts and minimize their transfers
                for (int j = (i - 1) & i; j > 0; j = (j - 1) & i) {
                    if (minTransfers[i] > minTransfers[j] + minTransfers[i ^ j]) {
                        minTransfers[i] = minTransfers[j] + minTransfers[i ^ j];
                        prevSubset[i] = j;
                    }
                }
            }
        }
        int optimalSubset = (1 << numAccounts) - 1;
        List<Transfer> optimalTransfers = reconstructTransfers(optimalSubset, nonZeroBalances, users, prevSubset);
        return optimalTransfers;

    }

    private static List<Transfer> reconstructTransfers(int subset, List<Double> nonZeroBalances, List<User> users, int[] prevSubset) {
        List<Transfer> optimalTransfers = new ArrayList<>();
        Date today = new Date();
        while (subset > 0) {
            int prev = prevSubset[subset];
            int diff = subset ^ prev;

            for (int j = 0; j < users.size(); ++j) {
                if ((diff >> j & 1) == 1) {
                    int k = -1;
                    for (int i = 0; i < users.size(); ++i) {
                        if (i != j && nonZeroBalances.get(i) != 0 && (subset >> i & 1) == 1) {
                            k = i;
                            break;
                        }
                    }
                    Transfer transfer;
                    Double amount = nonZeroBalances.get(j);
                    if (amount == 0) continue;

                    if (amount > 0)
                        transfer = new Transfer("", today, Money.of(nonZeroBalances.get(j), Format.CURRENCY), users.get(k), users.get(j));
                    else
                        transfer = new Transfer("", today, Money.of(-nonZeroBalances.get(j), Format.CURRENCY), users.get(j), users.get(k));

                    nonZeroBalances.set(k, nonZeroBalances.get(k) + amount);
                    nonZeroBalances.set(j, 0.0);
                    optimalTransfers.add(transfer);
                }
            }
            subset = prev;
        }
        return optimalTransfers;
    }

    private static int findIndex(List<User> users, List<Double> nonZeroBalances, int subset, int j) {
        double balance = nonZeroBalances.get(j);

        for (int i = j + 1; i < users.size(); ++i) {
            if ((subset >> i & 1) == 1) {
                return i;
            }
        }

        return -1; // Handle error case
    }

}
