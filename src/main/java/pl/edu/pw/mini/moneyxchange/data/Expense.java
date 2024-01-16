package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Expense implements MoneyAction, Serializable {
    private final User payer;
    private final Money amount;
    private final Map<User, Money> debts;
    private String name; //TODO: proof-of-concept aktualizowania zmiennych
    private final Date date;
    private final ExpenseCategory category;

    private SwingPropertyChangeSupport propertyChangeSupport;

    public Expense(User payer, Money amount, Map<User, Money> debts,
                   String name, Date date, ExpenseCategory category) {
        this.payer = payer;
        this.amount = amount;
        this.debts = debts;
        this.name = name;
        this.date = date;
        this.category = category;

        this.propertyChangeSupport = new SwingPropertyChangeSupport(this);

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

    public void setName(String name) {
        propertyChangeSupport.firePropertyChange("name", this.name, name);
        this.name = name;
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

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public JPanel getPanel() {
        return new ExpensePanel();
    }

    public class ExpensePanel extends JPanel implements PropertyChangeListener {

        private JLabel titleLabel;
        private JLabel dateLabel;
        private JLabel amountLabel;
        private JLabel payerLabel;
        private JLabel debtsLabel;

        public ExpensePanel() {
            super();

            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setLayout(new GridLayout(0, 1));

            titleLabel = new JLabel("Tytuł: " + name);
            dateLabel = new JLabel("Data: " + Format.SIMPLE_DATE_FORMAT.format(date));
            amountLabel = new JLabel("Kwota: " + Format.MONETARY_FORMAT.format(amount));
            payerLabel = new JLabel("Zapłacone przez: " + payer.getName());
            debtsLabel = new JLabel("Długi: ");

            add(titleLabel);
            add(dateLabel);
            add(amountLabel);
            add(payerLabel);
            add(debtsLabel);

            Expense.this.addListener(this);

            for (var entry : debts.entrySet()) {
                JLabel label = new JLabel("• " +
                        entry.getKey().getName() + ": " +
                        Format.MONETARY_FORMAT.format(entry.getValue()));
                add(label);
            }

//        JLabel usersLabel = new JLabel("Użytkownicy: " + String.join(", ",
//                getParticipants().stream().map(User::getName).toArray(String[]::new)));
//        expensePanel.add(usersLabel);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();
            if (property.equals("name")) {
                titleLabel.setText("Tytuł: " + evt.getNewValue());
            }
        }
    }
}
