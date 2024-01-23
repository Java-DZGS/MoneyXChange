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
     * The date of the transfer.
     */
    private Date date;

    /**
     * The amount of money being transferred.
     */
    private Money amount;

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
     * @param date     The date of the transfer.
     * @param amount   The amount of money being transferred.
     * @param fromUser The user sending the transfer.
     * @param toUser   The user receiving the transfer.
     */
    public Transfer(Date date, Money amount, User fromUser, User toUser) {
        this.date = date;
        this.amount = amount;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    /**
     * Gets the date of the transfer.
     *
     * @return The date of the transfer.
     */
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) { this.date = date;}

    /**
     * Gets the amount of money being transferred.
     *
     * @return The amount of money being transferred.
     */
    public Money getAmount() {
        return amount;
    }
    public void setAmount(Money amount) { this.amount = amount;}

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


    @Override
    public String toString() {
        return "Transfer{" +
                "date=" + date +
                ", amount=" + Format.MONETARY_FORMAT.format(amount) +
                ", fromUser=" + fromUser +
                ", toUser=" + toUser +
                '}';
    }

    public String getName()
    {
        return "Przelew od " + fromUser.getName() + " do " + toUser.getName();
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

            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
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

            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;

            JLabel usersLabel = new JLabel("Przelew od " + fromUser.getName() + " do " + toUser.getName());
            JLabel amountLabel = new JLabel("Kwota: " + Format.MONETARY_FORMAT.format(amount));

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1.0;
            add(usersLabel, constraints);

            JButton completeButton = new JButton("Zrób przelew");
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.weightx = 0.5;
            constraints.anchor = GridBagConstraints.EAST;
            add(completeButton, constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            constraints.weightx = 1.0;
            constraints.anchor = GridBagConstraints.WEST;
            add(amountLabel, constraints);

            completeButton.addActionListener(e -> {
                CompleteTransferDialog completeTransferDialog = new CompleteTransferDialog(Group.getInstance(), Transfer.this);
                completeTransferDialog.setSize(300, 200);
                completeTransferDialog.setLocationRelativeTo(null);
                completeTransferDialog.setVisible(true);
            });
        }
    }
}
