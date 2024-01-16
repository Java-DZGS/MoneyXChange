package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.dialogs.CompleteTransferDialog;
import pl.edu.pw.mini.moneyxchange.dialogs.ExpenseDialog;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Date;

public class Transfer implements MoneyAction, Serializable {
    // to edit
    private final String title;
    private final Date date;
    private final Money amount;
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

    public Money getAmount() {
        return amount;
    }

    public User getToUser() {
        return toUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    public JPanel getPanel()
    {
        JPanel transferPanel = new JPanel();
        transferPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        transferPanel.setLayout(new GridLayout(3, 1));

        JLabel titleLabel = new JLabel("Przelew od " + fromUser.getName() + " do " + toUser.getName());
        JLabel dateLabel = new JLabel("Data: " + Format.SIMPLE_DATE_FORMAT.format(date));
        JLabel amountLabel = new JLabel("Kwota: " + Format.MONETARY_FORMAT.format(amount));

        transferPanel.add(titleLabel);
        transferPanel.add(dateLabel);
        transferPanel.add(amountLabel);

        return transferPanel;
    }
    public JPanel getOptimalPanel() {
        JPanel transferPanel = new JPanel(new GridBagLayout());
        transferPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST; // Ustawienie anchor na WEST wyrówna elementy do lewej

        JLabel usersLabel = new JLabel("Przelew od " + fromUser.getName() + " do " + toUser.getName());
        JLabel titleLabel = new JLabel("Tytuł: " + getTitle());
        JLabel amountLabel = new JLabel("Kwota: " + Format.MONETARY_FORMAT.format(amount));

        constraints.gridx = 0;
        constraints.gridy = 0;
        transferPanel.add(usersLabel, constraints);

        constraints.gridy = 1;
        transferPanel.add(titleLabel, constraints);

        constraints.gridy = 2;
        transferPanel.add(amountLabel, constraints);

        JButton completeButton = new JButton("Zrób przelew");
        constraints.anchor = GridBagConstraints.EAST; // Ustawienie anchor na EAST wyrówna przycisk do prawej
        constraints.gridx = 1;
        constraints.gridy = 1;
        transferPanel.add(completeButton, constraints);

        completeButton.addActionListener(e -> {
            CompleteTransferDialog completeTransferDialog = new CompleteTransferDialog(Group.getInstance(), this);
        });

        return transferPanel;
    }
}


