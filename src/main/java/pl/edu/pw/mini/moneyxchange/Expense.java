package pl.edu.pw.mini.moneyxchange;

import java.io.Serializable;

public class Expense implements Serializable {
    private String title;
    private String date;
    private double amount;
    private String payer;
    private String category;

    public Expense(String title, String date, double amount, String payer, String category) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.payer = payer;
        this.category = category;
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

    public String getPayer() {
        return payer;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", amount=" + amount +
                ", payer='" + payer + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
