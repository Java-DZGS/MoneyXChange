package pl.edu.pw.mini.moneyxchange.cashflow;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.Transfer;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Utility class for minimizing cash flow in a group by determining optimal transfers between users.
 * <br>
 * <br>
 * The algorithm proceeds through the following steps:<br>
 * <b>Step 1</b>: Calculating Net Balances<br>
 * <ul>
 *     <li>Prepares a list to store the net balance of each person.</li>
 *     <li>Iterates through the list of financial transactions, updating net balances accordingly.</li>
 *     <li>A positive balance implies giving money, while a negative balance implies receiving money.</li>
 * </ul>
 * <b>Step 2</b>: Filtering Zero Net Balances<br>
 * <ul>
 *     <li>Filters out individuals with a net balance of zero, as they do not need to be involved in further transactions.</li>
 * </ul>
 * <b>Step 3</b>: Initializing Dynamic Programming Array<br>
 * <ul>
 *     <li>Initializes a dynamic programming array {@code f} to track the minimum number of transactions for each subset of persons.</li>
 * </ul>
 * <b>Step 4</b>: Finding Minimum Number of Transactions<br>
 * <ul>
 *     <li>Iterates through all possible non-zero net balance subsets, using a bitmask {@code i}.</li>
 *     <li>Computes the sum of balances in the subset; if the sum is zero, it indicates a subset that can balance itself.</li>
 * </ul>
 * <b>Step 5</b>: Setting Base Condition for Correct Subsets<br>
 * <ul>
 *     <li>If the sum for the 'i'-th subset is zero, calculates the number of required transactions as one less than the number of set bits in 'i'.</li>
 *     <li>Transactions can be executed by selecting any two individuals with non-zero balances and transferring funds
 *     to zero out the balance of one of them. Repeats this step until all debts in the group are settled.</li>
 * </ul>
 * <b>Step 6</b>: Optimizing Transactions<br>
 * <ul>
 *     <li>Searches for pairs of disjoint subsets {@code j} and {@code i^j} (XOR operation) that combine into the subset {@code i}.</li>
 *     <li>Uses bitwise manipulation to iterate through potential pairs, aiming to minimize the sum {@code f[j] + f[i^j]}.</li>
 *     <li>Stores the optimal previous subset {@code prevSubset[i] = j}.</li>
 * </ul>
 * <b>Step 7:</b> Reconstructing Optimal Transfers<br>
 * <ul>
 *     <li>Iterates through all persons, reading the previous optimal subset {@code prev}, and its complement {@code diff}.</li>
 *     <li>In a for loop checking for differences between the current and previous subsets, finds a partner for settlement.</li>
 *     <li>Creates a {@link Transfer} object representing the transaction and updates balances.</li>
 * </ul>
 * <b>Step 8</b>: Returning the List of Optimal Transfers<br>
 * <ul>
 *     <li>Returns a list of {@link Transfer} objects representing optimal transactions to balance debts within the obtained optimal subset.</li>
 * </ul>
 */
public class MinCashFlow {
    /**
     * Calculates the minimal transfers required to equalize balances within a group.<br>
     *
     * @param transfers List of Transfer objects representing monetary transactions to be completed between users.
     * @return List of Transfer objects representing optimal transfers to minimize cash flow.
     */
    public static List<Transfer> minTransfers(List<Transfer> transfers) {
        int max = transfers.stream()
                .flatMapToInt(transfer -> IntStream.of(transfer.getFromUser().getId(), transfer.getToUser().getId()))
                .max()
                .orElse(0);
        Money[] balance = new Money[max + 1];
        User[] userIds = new User[max + 1];
        int userCount = 0;

        for (int i = 0; i <= max; i++)
            balance[i] = Money.of(0, Format.CURRENCY);

        for (var transfer : transfers) {
            int from = transfer.getFromUser().getId();
            int to = transfer.getToUser().getId();

            balance[from] = balance[from].add(transfer.getAmount());
            balance[to] = balance[to].subtract(transfer.getAmount());
            userIds[from] = transfer.getFromUser();
            userIds[to] = transfer.getToUser();
        }

        int index = 0;
        List<Money> nonZeroBalances = new ArrayList<>();
        List<User> users = new ArrayList<>();
        for (Money b : balance) {
            if (!b.isZero()) {
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
            Money sum = Money.of(0, Format.CURRENCY);

            for (int j = 0; j < numAccounts; ++j) {
                if ((i >> j & 1) == 1) {
                    sum = sum.add(nonZeroBalances.get(j));
                }
            }

            if (sum.isZero()) {
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
    private static List<Transfer> reconstructTransfers(int subset, List<Money> nonZeroBalances, List<User> users, int[] prevSubset) {
        List<Transfer> optimalTransfers = new ArrayList<>();
        Date today = new Date();
        while (subset > 0) {
            int prev = prevSubset[subset];
            int diff = subset ^ prev;

            for (int j = 0; j < users.size(); ++j) {
                if ((diff >> j & 1) == 1) {
                    int k = -1;
                    for (int i = 0; i < users.size(); ++i) {
                        if (i != j && !nonZeroBalances.get(i).isZero() && (subset >> i & 1) == 1) {
                            k = i;
                            break;
                        }
                    }

                    Transfer transfer;
                    Money amount = nonZeroBalances.get(j);
                    if (amount.isZero()) continue;

                    if (amount.isPositive())
                        transfer = new Transfer(today, nonZeroBalances.get(j), users.get(j), users.get(k));
                    else
                        transfer = new Transfer(today, nonZeroBalances.get(j).negate(), users.get(k), users.get(j));

                    nonZeroBalances.set(k, nonZeroBalances.get(k).add(amount));
                    nonZeroBalances.set(j, Money.of(0, Format.CURRENCY));
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
