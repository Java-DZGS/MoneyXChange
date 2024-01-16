package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.dialogs.CompleteTransferDialog;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Date;

public class Transfer implements MoneyAction, Serializable {
    private final String title;
    private Date date;
    private Money amount;
    private final User fromUser;
    private final User toUser;

    public Transfer(String title, Date date, Money amount, User fromUser, User toUser) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) { this.date = date;}

    public Money getAmount() {
        return amount;
    }
    public void setAmount(Money amount) { this.amount = amount;}

    public User getToUser() {
        return toUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    public JPanel getPanel() {
        return new TransferPanel();
    }

    public JPanel getOptimalPanel() {
        return new OptimalTransferPanel();
    }

    public JPanel getPendingPanel() {
        return new PendingTransferPanel();
    }

    public class TransferPanel extends JPanel {
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

    public class OptimalTransferPanel extends JPanel {
        public OptimalTransferPanel() {
            super(new GridBagLayout());

            setBorder(BorderFactory.createLineBorder(Color.BLACK));

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST; // Ustawienie anchor na WEST wyrówna elementy do lewej

            JLabel usersLabel = new JLabel("Przelew od " + fromUser.getName() + " do " + toUser.getName());
            JLabel titleLabel = new JLabel("Tytuł: " + getTitle());
            JLabel amountLabel = new JLabel("Kwota: " + Format.MONETARY_FORMAT.format(amount));

            constraints.gridx = 0;
            constraints.gridy = 0;
            add(usersLabel, constraints);

            constraints.gridy = 1;
            add(titleLabel, constraints);

            constraints.gridy = 2;
            add(amountLabel, constraints);

            JButton completeButton = new JButton("Zrób przelew");
            constraints.anchor = GridBagConstraints.EAST; // Ustawienie anchor na EAST wyrówna przycisk do prawej
            constraints.gridx = 1;
            constraints.gridy = 1;
            add(completeButton, constraints);

            completeButton.addActionListener(e -> {
                CompleteTransferDialog completeTransferDialog = new CompleteTransferDialog(Group.getInstance(), Transfer.this);
                // TODO
            });
        }
    }

    public class PendingTransferPanel extends TransferPanel {

        public PendingTransferPanel() {
            JButton doneButton = new JButton("Oznacz jako zrobiony");
            add(doneButton);
            doneButton.addActionListener(e -> Transfer.this.getFromUser().addCompletedTransfer(Transfer.this));
        }
    }
}


