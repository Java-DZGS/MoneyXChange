package pl.edu.pw.mini.moneyxchange;
import java.util.List;

public class Transfer {
    private final String title;
    private final String date;
    private final double amount;
    private final List<String> participants;

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


