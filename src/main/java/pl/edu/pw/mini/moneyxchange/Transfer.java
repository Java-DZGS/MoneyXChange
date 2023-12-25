package pl.edu.pw.mini.moneyxchange;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Transfer {
    private String title;
    private String date;
    private double amount;
    private List<String> participants;

    public Transfer(String title, String date, double amount, List<String> participants) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.participants = participants;
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

    public List<String> getParticipants() {
        return participants;
    }
}


