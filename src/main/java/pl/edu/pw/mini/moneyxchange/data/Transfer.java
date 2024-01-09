package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
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
}


