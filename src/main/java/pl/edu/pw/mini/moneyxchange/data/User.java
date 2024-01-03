package pl.edu.pw.mini.moneyxchange.data;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// TODO
public class User implements Serializable {

    private String name;
    private BufferedImage image;
    private List<Expense> expenses;
    private List<Transfer> pendingTransfers;
    private List<Transfer> completedTransfers;

    public User(String name) {
        this.name = name;
        this.expenses = new ArrayList<>();
        this.pendingTransfers = new ArrayList<>();
        this.completedTransfers = new ArrayList<>();
    }
    public String getName() {
        return name;
    }
    public List<Expense> getExpenses(){ return expenses; }
    public void addExpense(Expense expense){
        expenses.add(expense);
    }
    public List<Transfer> getPendingTransfers() { return pendingTransfers; }
    public void addPendingTransfer(Transfer transfer){
        pendingTransfers.add(transfer);
    }
    public List<Transfer> getCompletedTransfers() { return completedTransfers; }
    public void addCompletedTransfer(Transfer transfer){
        pendingTransfers.remove(transfer);
        completedTransfers.add(transfer);
    }
    public void setImage(BufferedImage image){
        this.image = image;
    }
}
