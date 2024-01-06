package pl.edu.pw.mini.moneyxchange.data;

import java.io.Serializable;
import java.util.*;

public class Expense implements MoneyAction, Serializable {
    private final User payer;
    private final double amount;
    private final HashMap<User, Double> debts;
    private final String name;
    private final Date date;
    private final ExpenseCategory category;

    public Expense(User creator, double amount, HashMap<User, Double> debts,
                   String name, Date date, ExpenseCategory category) {
        this.payer = creator;
        this.amount = amount;
        this.debts = debts;
        this.name = name;
        this.date = date;
        this.category = category;
    }

    public User getPayer() {
        return payer;
    }

    public double getAmount() {
        return amount;
    }

    public List<User> getParticipants()
    {
        // Get users with a non-zero debt
        List<User> ret = new ArrayList<User>();
        for (Map.Entry<User, Double> entry : debts.entrySet()) {
            if (entry.getValue() != 0) {
                ret.add(entry.getKey());
            }
        }
        return ret;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "creator=" + payer +
                ", amount=" + amount +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
