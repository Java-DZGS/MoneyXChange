package pl.edu.pw.mini.moneyxchange.data;

import java.io.Serializable;
import java.util.List;

public class Expense implements Serializable {
    private final User creator;
    private final double amount;
    private final List<User> participants;
    private final DivisionType divisionType;
    private final String name;
    private final String date;

    public Expense(User creator, double amount, List<User> participants, DivisionType divisionType, String name, String date) {
        this.creator = creator;
        this.amount = amount;
        this.participants = participants;
        this.divisionType = divisionType;
        this.name = name;
        this.date = date;
    }

    public User getCreator() {
        return creator;
    }

    public double getAmount() {
        return amount;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public DivisionType getDivisionType() {
        return divisionType;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "creator=" + creator +
                ", amount=" + amount +
                ", participants=" + participants +
                ", divisionType=" + divisionType +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
