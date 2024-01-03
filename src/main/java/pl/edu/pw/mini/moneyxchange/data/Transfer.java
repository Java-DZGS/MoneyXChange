package pl.edu.pw.mini.moneyxchange;

public class Transfer {
    // to edit
    private final String title;
    private final String date;
    private final double amount;
    private final User fromUser;
    private final User toUser;

    public Transfer(String title, String date, double amount, User fromUser, User toUser) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
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


