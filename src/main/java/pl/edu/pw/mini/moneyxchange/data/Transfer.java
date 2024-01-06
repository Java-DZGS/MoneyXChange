package pl.edu.pw.mini.moneyxchange.data;

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
}


