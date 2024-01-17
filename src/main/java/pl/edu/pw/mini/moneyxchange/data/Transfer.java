package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.dialogs.CompleteTransferDialog;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Represents a financial transfer between two users.
 */
public class Transfer implements MoneyAction, Serializable {

    /**
     * The title of the transfer.
     */
    private final String title;

    /**
     * The date of the transfer.
     */
    private final Date date;

    /**
     * The amount of money being transferred.
     */
    private final Money amount;

    /**
     * The user sending the transfer.
     */
    private final User fromUser;

    /**
     * The user receiving the transfer.
     */
    private final User toUser;

    /**
     * Creates a new transfer with the specified details.
     *
     * @param title    The title of the transfer.
     * @param date     The date of the transfer.
     * @param amount   The amount of money being transferred.
     * @param fromUser The user sending the transfer.
     * @param toUser   The user receiving the transfer.
     */
    public Transfer(String title, Date date, Money amount, User fromUser, User toUser) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    /**
     * Gets the title of the transfer.
     *
     * @return The title of the transfer.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the date of the transfer.
     *
     * @return The date of the transfer.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the amount of money being transferred.
     *
     * @return The amount of money being transferred.
     */
    public Money getAmount() {
        return amount;
    }

    /**
     * Gets the user receiving the transfer.
     *
     * @return The user receiving the transfer.
     */
    public User getToUser() {
        return toUser;
    }

    /**
     * Gets the user sending the transfer.
     *
     * @return The user sending the transfer.
     */
    public User getFromUser() {
        return fromUser;
    }

    /**
     * Gets a panel representing the transfer.
     *
     * @return A panel representing the transfer.
     */
    public JPanel getPanel() {
        return new TransferPanel();
    }

    /**
     * Gets an optimal panel representing the transfer.
     *
     * @return An optimal panel representing the transfer.
     */
    public JPanel getOptimalPanel() {
        return new OptimalTransferPanel();
    }

    /**
     * Gets a pending panel representing the transfer.
     *
     * @return A pending panel representing the transfer.
     */
    public JPanel getPendingPanel() {
        return new PendingTransferPanel();
    }

    /**
     * A panel representing the transfer.
     */
    public class TransferPanel extends JPanel {
        /**
         * Creates a transfer panel with basic information.
         */
        public TransferPanel() {
            super();

            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setLayout(new GridLayout(3, 1));

            JLabel titleLabel = new JLabel("Przelew od " + fromUser.getName() + " do " + toUser.getName());
            JLabel dateLabel = new JLabel("Data: " + Format.SIMPLE_DATE_FORMAT.format(date));
            JLabel amountLabel = new JLabel("Kwota: " + Format.MONETARY_FORMAT.format(amount));

            add(titleLabel);
            add(dateLabel);
            add(amountLabel);
        }
    }

    /**
     * A panel representing the optimal transfer.
     */
    public class OptimalTransferPanel extends JPanel {
        /**
         * Creates an optimal transfer panel with detailed information.
         */
        public OptimalTransferPanel() {
            super(new GridBagLayout());

            setBorder(BorderFactory.createLineBorder(Color.BLACK));

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;

            JLabel usersLabel = new JLabel("Przelew od " + fromUser.getName() + " do " + toUser.getName());
            JLabel titleLabel = new JLabel("Tytuł: " + getTitle());
            JLabel amountLabel = new JLabel("Kwota: " + Format.MONETARY_FORMAT.format(amount));

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1.0;
            add(usersLabel, constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.weightx = 1;
            add(titleLabel, constraints);

            JButton completeButton = new JButton("Zrób przelew");
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.weightx = 0.5;
            constraints.anchor = GridBagConstraints.EAST;
            add(completeButton, constraints);

            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 2;
            constraints.weightx = 1.0;
            constraints.anchor = GridBagConstraints.WEST;
            add(amountLabel, constraints);

            completeButton.addActionListener(e -> {
                CompleteTransferDialog completeTransferDialog = new CompleteTransferDialog(Group.getInstance(), Transfer.this);
                // TODO
            });
        }
    }

    /**
     * A transfer panel representing the pending transfer.
     */
    public class PendingTransferPanel extends TransferPanel {
        /**
         * Creates a pending transfer panel with a button to mark it as done.
         */
        public PendingTransferPanel() {
            JButton doneButton = new JButton("Oznacz jako zrobiony");
            add(doneButton);
            doneButton.addActionListener(e -> Transfer.this.getFromUser().addCompletedTransfer(Transfer.this));
        }
    }
}
