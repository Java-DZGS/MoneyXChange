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

/**
 * Utility class for minimizing cash flow in a group by determining optimal transfers between users.
 */
public class MinCashFlow {
    /**
     * Calculates the minimal transfers required to equalize balances within a group.
     *
     * @param transfers List of Transfer objects representing monetary transactions to be completed between users.
     * @return List of Transfer objects representing optimal transfers to minimize cash flow.
     * @implSpec The algorithm proceeds through the following steps:
     * <br>
     * Step 1: Calculating Net Balances<br>
     * - Prepares a list to store the net balance of each person.<br>
     * - Iterates through the list of financial transactions, updating net balances accordingly.<br>
     * A positive balance implies giving money, while a negative balance implies receiving money.<br>
     * <br>
     * Step 2: Filtering Zero Net Balances<br>
     * - Filters out individuals with a net balance of zero, as they do not need to be involved in further transactions.<br>
     * <br>
     * Step 3: Initializing Dynamic Programming Array<br>
     * - Initializes a dynamic programming array 'f' to track the minimum number of transactions for each subset of persons.<br>
     * <br>
     * Step 4: Finding Minimum Number of Transactions<br>
     * - Iterates through all possible non-zero net balance subsets, using a bitmask 'i'.<br>
     * - Computes the sum of balances in the subset; if the sum is zero, it indicates a subset that can balance itself.<br>
     * <br>
     * Step 5: Setting Base Condition for Correct Subsets<br>
     * - If the sum for the 'i'-th subset is zero, calculates the number of required transactions as one less than the number of set bits in 'i'.<br>
     * Transactions can be executed by selecting any two individuals with non-zero balances and transferring funds<br>
     * to zero out the balance of one of them. Repeats this step until all debts in the group are settled.<br>
     * <br>
     * Step 6: Optimizing Transactions<br>
     * - Searches for pairs of disjoint subsets 'j' and 'i^j' (XOR operation) that combine into the subset 'i'.<br>
     * - Uses bitwise manipulation to iterate through potential pairs, aiming to minimize the sum 'f[j] + f[i^j]'.<br>
     * - Stores the optimal previous subset 'prevSubset[i] = j'.<br>
     * <br>
     * Step 7: Reconstructing Optimal Transfers<br>
     * - Iterates through all persons, reading the previous optimal subset 'prev', and its complement 'diff'.<br>
     * - In a for loop checking for differences between the current and previous subsets, finds a partner for settlement.<br>
     * - Creates a Transfer object representing the transaction and updates balances.<br>
     * <br>
     * Step 8: Returning the List of Optimal Transfers<br>
     * - Returns a list of Transfer objects representing optimal transactions to balance debts within the obtained optimal subset.
     */
    public static List<Transfer> minTransfers(List<Transfer> transfers) {
        int max = transfers.stream()
                .flatMapToInt(transfer -> IntStream.of(transfer.getFromUser().getId(), transfer.getToUser().getId()))
                .max()
                .orElse(0);
        double[] balance = new double[max + 1];
        User[] userIds = new User[max + 1];
        int userCount = 0;

        for (var transfer : transfers) {
            balance[transfer.getFromUser().getId()] += transfer.getAmount().getNumber().doubleValue();
            balance[transfer.getToUser().getId()] -= transfer.getAmount().getNumber().doubleValue();
            userIds[transfer.getFromUser().getId()] = transfer.getFromUser();
            userIds[transfer.getToUser().getId()] = transfer.getToUser();
        }

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

        int numAccounts = nonZeroBalances.size();
        int[] minTransfers = new int[1 << numAccounts];
        Arrays.fill(minTransfers, Integer.MAX_VALUE / 2);
        minTransfers[0] = 0;

        int[] prevSubset = new int[1 << numAccounts];

        // Loop through all possible subsets of debts
        for (int i = 1; i < (1 << numAccounts); ++i) {
            int sum = 0;

            for (int j = 0; j < numAccounts; ++j) {
                if ((i >> j & 1) == 1) {
                    sum += nonZeroBalances.get(j);
                }
            }

            if (sum == 0) {
                minTransfers[i] = Integer.bitCount(i) - 1;

                for (int j = (i - 1) & i; j > 0; j = (j - 1) & i) {
                    if (minTransfers[i] > minTransfers[j] + minTransfers[i ^ j]) {
                        minTransfers[i] = minTransfers[j] + minTransfers[i ^ j];
                        prevSubset[i] = j;
                    }
                }
            }
        }
        int optimalSubset = (1 << numAccounts) - 1;
        return reconstructTransfers(optimalSubset, nonZeroBalances, users, prevSubset);

    }

    /**
     * Reconstructs the optimal transfers based on the subset of users involved in the cash flow minimization.
     *
     * @param subset          Subset of users involved in the cash flow minimization.
     * @param nonZeroBalances List of non-zero balances for users in the group.
     * @param users           List of User objects representing users in the group.
     * @param prevSubset      Array containing information about the previous subset in the optimization process.
     * @return List of Transfer objects representing optimal transfers to minimize cash flow within the subset.
     */
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
                        transfer = new Transfer(today, Money.of(nonZeroBalances.get(j), Format.CURRENCY), users.get(j), users.get(k));
                    else
                        transfer = new Transfer(today, Money.of(-nonZeroBalances.get(j), Format.CURRENCY), users.get(k), users.get(j));

                    nonZeroBalances.set(k, nonZeroBalances.get(k) + amount);
                    nonZeroBalances.set(j, 0.0);
                    optimalTransfers.add(transfer);
                }
            }
            subset = prev;
        }
        return optimalTransfers;
    }

    /**
     * Finds the index of the next user in the subset with a non-zero balance after the given index.
     *
     * @param users           List of User objects representing users in the group.
     * @param nonZeroBalances List of non-zero balances for users in the group.
     * @param subset          Current subset of users
     * @param j               Current index in the subset.
     * @return Index of the next user in the subset with a non-zero balance, or -1 if none found.
     */
    private static int findIndex(List<User> users, List<Double> nonZeroBalances, int subset, int j) {
        double balance = nonZeroBalances.get(j);

        for (int i = j + 1; i < users.size(); ++i) {
            if ((subset >> i & 1) == 1) {
                return i;
            }
        }
        return -1;
    }
}
