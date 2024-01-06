package pl.edu.pw.mini.moneyxchange.data;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Date;

public class Transfer implements MoneyAction, Serializable {
    // to edit
    private final String title;
    private final Date date;
    private final double amount;
    private final User fromUser;
    private final User toUser;

    public Transfer(String title, Date date, double amount, User fromUser, User toUser) {
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

    public double getAmount() {
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

        JLabel titleLabel = new JLabel("Przelew od " + fromUser + " do " + toUser);
        JLabel dateLabel = new JLabel("Data: " + date);
        JLabel amountLabel = new JLabel("Kwota: " + amount);

        transferPanel.add(titleLabel);
        transferPanel.add(dateLabel);
        transferPanel.add(amountLabel);

        return transferPanel;
    }
}


