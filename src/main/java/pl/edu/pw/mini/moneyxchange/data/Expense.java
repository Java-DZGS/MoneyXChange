package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Expense implements MoneyAction, Serializable {
    private final User payer;
    private final Money amount;
    private final Map<User, Money> debts;
    private final String name;
    private final Date date;
    private final ExpenseCategory category;

    public Expense(User payer, Money amount, Map<User, Money> debts,
                   String name, Date date, ExpenseCategory category) {
        this.payer = payer;
        this.amount = amount;
        this.debts = debts;
        this.name = name;
        this.date = date;
        this.category = category;

        payer.addExpense(this);

        for (var entry : debts.entrySet()) {
            User user = entry.getKey();
            Money debtAmount = entry.getValue();
            if (user.getId() == payer.getId()) continue;
            Transfer transfer = new Transfer(name, date, debtAmount, user, payer);
            user.addPendingTransfer(transfer);
        }
    }

    public User getPayer() {
        return payer;
    }

    public Money getAmount() {
        return amount;
    }

    public List<User> getParticipants() {
        // Get users with a non-zero debt
        return debts.entrySet().stream()
                .filter(entry -> entry.getValue().getNumber().doubleValue() != 0)
                .map(Map.Entry::getKey)
                .toList();
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
        JLabel dateLabel = new JLabel("Data: " + Format.SIMPLE_DATE_FORMAT.format(date));
        JLabel amountLabel = new JLabel("Kwota: " + Format.MONETARY_FORMAT.format(amount));
        JLabel payerLabel = new JLabel("Zapłacone przez: " + payer.getName());
        JLabel debtsLabel = new JLabel("Długi: ");

        expensePanel.add(titleLabel);
        expensePanel.add(dateLabel);
        expensePanel.add(amountLabel);
        expensePanel.add(payerLabel);
        expensePanel.add(debtsLabel);

        for (var entry : debts.entrySet()) {
            JLabel label = new JLabel("- " +
                    entry.getKey().getName() + ": " +
                    Format.MONETARY_FORMAT.format(entry.getValue()));
            expensePanel.add(label);
        }

//        JLabel usersLabel = new JLabel("Użytkownicy: " + String.join(", ",
//                getParticipants().stream().map(User::getName).toArray(String[]::new)));
//        expensePanel.add(usersLabel);

        return expensePanel;
    }
}
