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

/**
 * Represents an expense made by a user, involving multiple participants and debts.
 */
public class Expense implements MoneyAction, Serializable {

    /**
     * The user who paid for the expense.
     */
    private final User payer;

    /**
     * The total amount of the expense.
     */
    private final Money amount;

    /**
     * Map representing debts of each participant towards the payer.
     */
    private final Map<User, Money> debts;

    /**
     * The name/title of the expense.
     */
    private String name; //TODO: proof-of-concept aktualizowania zmiennych

    /**
     * The date when the expense occurred.
     */
    private final Date date;

    /**
     * The category of the expense.
     */
    private final ExpenseCategory category;

    /**
     * Support for property change events.
     */
    private SwingPropertyChangeSupport propertyChangeSupport;

    /**
     * Creates a new expense with the specified details.
     *
     * @param payer    The user who paid for the expense.
     * @param amount   The total amount of the expense.
     * @param debts    Map representing debts of each participant towards the payer.
     * @param name     The name/title of the expense.
     * @param date     The date when the expense occurred.
     * @param category The category of the expense.
     */
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

    /**
     * Gets the user who paid for the expense.
     *
     * @return The user who paid for the expense.
     */
    public User getPayer() {
        return payer;
    }

    /**
     * Gets the total amount of the expense.
     *
     * @return The total amount of the expense.
     */
    public Money getAmount() {
        return amount;
    }

    /**
     * Gets the participants in the expense (users with non-zero debt).
     *
     * @return The participants in the expense.
     */
    public List<User> getParticipants() {
        // Get users with a non-zero debt
        return debts.entrySet().stream()
                .filter(entry -> entry.getValue().getNumber().doubleValue() != 0)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Gets the name/title of the expense.
     *
     * @return The name/title of the expense.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name/title of the expense.
     *
     * @param name The new name/title of the expense.
     */
    public void setName(String name) {
        propertyChangeSupport.firePropertyChange("name", this.name, name);
        this.name = name;
    }

    /**
     * Gets the date when the expense occurred.
     *
     * @return The date when the expense occurred.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets a string representation of the expense.
     *
     * @return A string representation of the expense.
     */
    @Override
    public String toString() {
        return "Expense{" +
                "creator=" + payer +
                ", amount=" + amount +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    /**
     * Adds a property change listener to the expense.
     *
     * @param listener The listener to be added.
     */
    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Gets a panel representing the expense.
     *
     * @return A panel representing the expense.
     */
    public JPanel getPanel() {
        return new ExpensePanel();
    }

    /**
     * A panel representing the expense.
     */
    public class ExpensePanel extends JPanel implements PropertyChangeListener {

        private JLabel titleLabel;
        private JLabel dateLabel;
        private JLabel amountLabel;
        private JLabel payerLabel;
        private JLabel debtsLabel;

        /**
         * Creates an expense panel with basic information.
         */
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
