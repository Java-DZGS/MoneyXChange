package pl.edu.pw.mini.moneyxchange;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static Group instance;

    private String name = "Unnamed group 1";
    private final ArrayList<User> users;
    private final ArrayList<Expense> expenses;
    private final ArrayList<Transfer> pendingTransfers;
    private final ArrayList<Transfer> completedTransfers;

    private Group() {
        // Private constructor to prevent instantiation
        users = new ArrayList<>();
        expenses = new ArrayList<>();
        pendingTransfers = new ArrayList<>();
        completedTransfers = new ArrayList<>();

        users.add(new User("Bronisław"));
        users.add(new User("Stanisław"));
        users.add(new User("Radosław"));
        users.add(new User("Władysław"));
    }

    public static synchronized Group getInstance() {
        if (instance == null) {
            instance = new Group();
        }
        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    public ArrayList<Transfer> getPendingTransfers() {
        return pendingTransfers;
    }

    public ArrayList<Transfer> getCompletedTransfers() {
        return completedTransfers;
    }

    public void addPendingTransfer(Transfer Transfer) {
        pendingTransfers.add(Transfer);
    }

    public void markTransferAsCompleted(Transfer Transfer) {
        pendingTransfers.remove(Transfer);
        completedTransfers.add(Transfer);
    }

    public void serialize() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("group.ser"))) {
            oos.writeObject(instance);
        } catch (IOException e) {
            System.out.println("Nie udało się zserializować grupy");
        }
    }

    public static Group deserialize() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("group.ser"))) {
            return (Group) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Nie udało się zdeserializować grupy");
            return null;
        }
    }
}
