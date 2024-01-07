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

        payer.addExpense(this);
        for (Map.Entry<User, Double> entry : debts.entrySet()) {
            User user = entry.getKey();
            Double debtAmount = entry.getValue();
            Transfer transfer = new Transfer(name,date,debtAmount,user,payer);
            user.addPendingTransfer(transfer);
        }
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
        expensePanel.setLayout(new GridLayout(4, 1));

        JLabel titleLabel = new JLabel("Tytuł: " + name);
        JLabel dateLabel = new JLabel("Data: " + date);
        JLabel amountLabel = new JLabel("Kwota: " + amount);
        JLabel usersLabel = new JLabel("Użytkownicy: " + String.join(", ",
                getParticipants().stream().map(User::getName).toArray(String[]::new)));

        expensePanel.add(titleLabel);
        expensePanel.add(dateLabel);
        expensePanel.add(amountLabel);
        expensePanel.add(usersLabel);

        return expensePanel;
    }
}
