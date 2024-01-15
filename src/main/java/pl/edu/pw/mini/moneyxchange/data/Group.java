package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Group implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static Group instance;

    private String name = "Unnamed group 1";
    private final List<User> users;
    private final List<Expense> expenses;

    private final List<Transfer> pendingTransfers;
    private final List<Transfer> completedTransfers;

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

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<Transfer> getPendingTransfers() {
        return pendingTransfers;
    }

    public List<Transfer> getCompletedTransfers() {
        return completedTransfers;
    }

    public List<MoneyAction> getActionsList() {
        return Stream.concat(
                        expenses.stream().map(expense -> (MoneyAction) expense),
                        pendingTransfers.stream().map(transfer -> (MoneyAction) transfer))
                .sorted(Comparator.comparing(MoneyAction::getDate))
                .collect(Collectors.toList());
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

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

        try {
            File image = new File("test.png");
            users.get(0).setImage(ImageIO.read(image));

            image = new File("test2.png");
            users.get(4).setImage(ImageIO.read(image));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            pendingTransfers.add(new Transfer("Dinner", dateFormat.parse("2023-01-15"), Money.of(50.0, Format.CURRENCY), users.get(0), users.get(2)));
            pendingTransfers.add(new Transfer("Groceries", dateFormat.parse("2023-01-20"), Money.of(30.0, Format.CURRENCY), users.get(3), users.get(4)));
            pendingTransfers.add(new Transfer("Dinner", dateFormat.parse("2023-01-15"), Money.of(50.0, Format.CURRENCY), users.get(1), users.get(2)));
            pendingTransfers.add(new Transfer("Groceries", dateFormat.parse("2023-01-20"), Money.of(30.0, Format.CURRENCY), users.get(3), users.get(4)));
            pendingTransfers.add(new Transfer("Dinner", dateFormat.parse("2023-01-15"), Money.of(50.0, Format.CURRENCY), users.get(1), users.get(2)));
            pendingTransfers.add(new Transfer("Groceries", dateFormat.parse("2023-01-20"), Money.of(30.0, Format.CURRENCY), users.get(3), users.get(4)));
            pendingTransfers.add(new Transfer("Dinner", dateFormat.parse("2023-01-15"), Money.of(40.0, Format.CURRENCY), users.get(1), users.get(3)));
            pendingTransfers.add(new Transfer("Groceries", dateFormat.parse("2023-01-20"), Money.of(20.0, Format.CURRENCY), users.get(3), users.get(2)));

            Expense expense1 = new Expense(users.get(1), Money.of(50.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(0.0, Format.CURRENCY));
                        put(users.get(1), Money.of(25.0, Format.CURRENCY));
                        put(users.get(2), Money.of(25.0, Format.CURRENCY));
                        put(users.get(3), Money.of(0.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Pizza", dateFormat.parse("2023-01-01"), ExpenseCategory.FOOD
            );
            expenses.add(expense1);

            Expense expense2 = new Expense(users.get(4), Money.of(30.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(10.0, Format.CURRENCY));
                        put(users.get(1), Money.of(15.0, Format.CURRENCY));
                        put(users.get(2), Money.of(5.0, Format.CURRENCY));
                        put(users.get(3), Money.of(0.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Uber", dateFormat.parse("2023-01-02"), ExpenseCategory.TRANSPORT
            );
            expenses.add(expense2);

            Expense expense3 = new Expense(users.get(3), Money.of(45.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(10.0, Format.CURRENCY));
                        put(users.get(1), Money.of(15.0, Format.CURRENCY));
                        put(users.get(2), Money.of(10.0, Format.CURRENCY));
                        put(users.get(3), Money.of(10.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Movies", dateFormat.parse("2023-01-03"), ExpenseCategory.ENTERTAINMENT
            );
            users.get(3).addExpense(expense3);
            Expense expense4 = new Expense(users.get(1), Money.of(50.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(0.0, Format.CURRENCY));
                        put(users.get(1), Money.of(25.0, Format.CURRENCY));
                        put(users.get(2), Money.of(25.0, Format.CURRENCY));
                        put(users.get(3), Money.of(0.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Fries", dateFormat.parse("2023-01-01"), ExpenseCategory.FOOD
            );
            expenses.add(expense4);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
