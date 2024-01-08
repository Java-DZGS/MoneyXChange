package pl.edu.pw.mini.moneyxchange.data;

//import jdk.internal.access.JavaNetHttpCookieAccess;

import java.io.*;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        createDummyData();
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
    public void addUser(User user){
        users.add(user);
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

    public ArrayList<MoneyAction> getActionsList() {
        return (ArrayList<MoneyAction>) Stream.concat(
                        expenses.stream().map(expense -> (MoneyAction) expense),
                        pendingTransfers.stream().map(transfer -> (MoneyAction) transfer))
                .sorted(Comparator.comparing(MoneyAction::getDate))
                .collect(Collectors.toList());
    }

    public void addExpense(Expense expense) { expenses.add(expense); }
    public void addPendingTransfer(Transfer Transfer) {
        pendingTransfers.add(Transfer);
    }
    public void addCompletedTransfer(Transfer Transfer) {
        completedTransfers.add(Transfer);
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

    public User findUserByName(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user; // Found the user with the given name
            }
        }
        return null; // User not found
    }

    public void createDummyData() {
        users.add(new User("Bronisław", 0));
        users.add(new User("Stanisław", 1));
        users.add(new User("Radosław", 2));
        users.add(new User("Władysław", 3));
        users.add(new User("Krasnystaw", 4));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            pendingTransfers.add(new Transfer("Dinner", dateFormat.parse("2023-01-15"), 50.0, users.get(0), users.get(2)));
            pendingTransfers.add(new Transfer("Groceries", dateFormat.parse("2023-01-20"), 30.0, users.get(3), users.get(4)));
            pendingTransfers.add(new Transfer("Dinner", dateFormat.parse("2023-01-15"), 50.0, users.get(1), users.get(2)));
            pendingTransfers.add(new Transfer("Groceries", dateFormat.parse("2023-01-20"), 30.0, users.get(3), users.get(4)));
            pendingTransfers.add(new Transfer("Dinner", dateFormat.parse("2023-01-15"), 50.0, users.get(1), users.get(2)));
            pendingTransfers.add(new Transfer("Groceries", dateFormat.parse("2023-01-20"), 30.0, users.get(3), users.get(4)));
            pendingTransfers.add(new Transfer("Dinner", dateFormat.parse("2023-01-15"), 40.0, users.get(1), users.get(3)));
            pendingTransfers.add(new Transfer("Groceries", dateFormat.parse("2023-01-20"), 20.0, users.get(3), users.get(2)));

            Expense expense1 = new Expense(users.get(1), 50.0,
                    new HashMap<User, Double>() {{
                        put(users.get(0), 0.0);
                        put(users.get(1), 25.0);
                        put(users.get(2), 25.0);
                        put(users.get(3), 0.0);
                        put(users.get(4), 0.0);
                    }},
                    "Pizza", dateFormat.parse("2023-01-01"), ExpenseCategory.FOOD
            );
            expenses.add(expense1);

            Expense expense2 = new Expense(users.get(4), 30.0,
                    new HashMap<User, Double>() {{
                        put(users.get(0), 10.0);
                        put(users.get(1), 15.0);
                        put(users.get(2), 5.0);
                        put(users.get(3), 0.0);
                        put(users.get(4), 0.0);
                    }},
                    "Uber", dateFormat.parse("2023-01-02"), ExpenseCategory.TRANSPORT
            );
            expenses.add(expense2);

            Expense expense3 = new Expense(users.get(3), 45.0,
                    new HashMap<User, Double>() {{
                        put(users.get(0), 10.0);
                        put(users.get(1), 15.0);
                        put(users.get(2), 10.0);
                        put(users.get(3), 10.0);
                        put(users.get(4), 0.0);
                    }},
                    "Movies", dateFormat.parse("2023-01-03"), ExpenseCategory.ENTERTAINMENT
            );
            users.get(3).addExpense(expense3);
            Expense expense4 = new Expense(users.get(1), 50.0,
                    new HashMap<User, Double>() {{
                        put(users.get(0), 0.0);
                        put(users.get(1), 25.0);
                        put(users.get(2), 25.0);
                        put(users.get(3), 0.0);
                        put(users.get(4), 0.0);
                    }},
                    "Fries", dateFormat.parse("2023-01-01"), ExpenseCategory.FOOD
            );
            expenses.add(expense4);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
