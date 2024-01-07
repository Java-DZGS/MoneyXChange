package pl.edu.pw.mini.moneyxchange.data;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Expense implements MoneyAction, Serializable {
    private final User payer;
    private final double amount;
    private final HashMap<User, Double> debts;
    private final String name;
    private final Date date;
    private final ExpenseCategory category;

    public Expense(User payer, double amount, HashMap<User, Double> debts,
                   String name, Date date, ExpenseCategory category) {
        this.payer = payer;
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

    public JPanel getPanel() {
        JPanel expensePanel = new JPanel();
        expensePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        expensePanel.setLayout(new GridLayout(0, 1));

        JLabel titleLabel = new JLabel("Tytuł: " + name);
        JLabel dateLabel = new JLabel("Data: " + date);
        JLabel amountLabel = new JLabel("Kwota: " + amount);
        JLabel payerLabel = new JLabel("Zapłacone przez: " + payer.getName());
        JLabel debtsLabel = new JLabel("Długi: ");

        expensePanel.add(titleLabel);
        expensePanel.add(dateLabel);
        expensePanel.add(amountLabel);
        expensePanel.add(payerLabel);
        expensePanel.add(debtsLabel);

        for (Map.Entry<User, Double> entry : debts.entrySet()) {
            JLabel label = new JLabel("- " +
                    entry.getKey().getName() + ": " +
                    entry.getValue().toString());
            expensePanel.add(label);
        }

//        JLabel usersLabel = new JLabel("Użytkownicy: " + String.join(", ",
//                getParticipants().stream().map(User::getName).toArray(String[]::new)));
//        expensePanel.add(usersLabel);

        return expensePanel;
    }
}
