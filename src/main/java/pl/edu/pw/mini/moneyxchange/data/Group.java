package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.imageio.ImageIO;
import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.edu.pw.mini.moneyxchange.cashflow.MinCashFlow.minTransfers;

/**
 * Represents a group of users with shared expenses and transfers.
 */
public class Group implements Serializable {

    /**
     * The serial version UID for serialization.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The singleton instance of the group.
     */
    private static Group instance;

    /**
     * The name of the group.
     */
    private String name = "Grupa 1";

    /**
     * The list of users in the group.
     */
    private final List<User> users;

    /**
     * The list of expenses in the group.
     */
    private List<Expense> expenses;

    /**
     * The list of pending transfers in the group.
     */
    private List<Transfer> pendingTransfers;

    /**
     * The list of completed transfers in the group.
     */
    private List<Transfer> completedTransfers;

    /**
     * Support for property change events.
     */
    private SwingPropertyChangeSupport propertyChangeSupport;

    /**
     * Private constructor to prevent instantiation.
     */
    private Group() {
        users = new ArrayList<>();
        expenses = new ArrayList<>();
        pendingTransfers = new ArrayList<>();
        completedTransfers = new ArrayList<>();

        propertyChangeSupport = new SwingPropertyChangeSupport(this);

    }

    /**
     * Gets the singleton instance of the group.
     *
     * @return The singleton instance of the group.
     */
    public static synchronized Group getInstance() {
        if (instance == null) {
            instance = new Group();
            instance.createDummyData();
        }
        return instance;
    }

    /**
     * Gets the name of the group.
     *
     * @return The name of the group.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the group.
     *
     * @param name The new name for the group.
     */
    public void setName(String name) {
        propertyChangeSupport.firePropertyChange("name", this.name, name);
        this.name = name;
    }

    /**
     * Gets the list of users in the group.
     *
     * @return The list of users in the group.
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Adds a new user to the group.
     *
     * @param user The user to be added.
     */
    public void addUser(User user) {
        users.add(user);
        propertyChangeSupport.firePropertyChange("users", null, users);
    }

    /**
     * Gets the list of expenses in the group.
     *
     * @return The list of expenses in the group.
     */
    public List<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Gets the list of pending transfers in the group.
     *
     * @return The list of pending transfers in the group.
     */
    public List<Transfer> getPendingTransfers() {
        return pendingTransfers;
    }

    /**
     * Calculates the list of optimal transfers in the group.
     */
    public void calculatePendingTransfers(){
        pendingTransfers = minTransfers(pendingTransfers);
        propertyChangeSupport.firePropertyChange("pendingTransfers", null, pendingTransfers);
    }

    /**
     * Gets the list of completed transfers in the group.
     *
     * @return The list of completed transfers in the group.
     */
    public List<Transfer> getCompletedTransfers() {
        return completedTransfers;
    }

    /**
     * Gets the combined list of expenses and pending transfers sorted by date.
     *
     * @return The combined list of expenses and pending transfers sorted by date.
     */
    public List<MoneyAction> getActionsList() {
        return Stream.concat(
                        expenses.stream().map(expense -> (MoneyAction) expense),
                        pendingTransfers.stream().map(transfer -> (MoneyAction) transfer))
                .sorted(Comparator.comparing(MoneyAction::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Adds a new expense to the group.
     *
     * @param expense The expense to be added.
     */
    public void addExpense(Expense expense) {
        expenses.add(expense);
        propertyChangeSupport.firePropertyChange("expenses", null, expenses);
    }

    /**
     * Adds a new pending transfer to the group.
     *
     * @param transfer The pending transfer to be added.
     */
    public void addPendingTransfer(Transfer transfer) {
        pendingTransfers.add(transfer);
        propertyChangeSupport.firePropertyChange("pendingTransfers", null, pendingTransfers);
    }

    /**
     * Adds a new completed transfer to the group.
     *
     * @param transfer The completed transfer to be added.
     */
    public void addCompletedTransfer(Transfer transfer) {
        completedTransfers.add(transfer);
        propertyChangeSupport.firePropertyChange("completedTransfers", null, completedTransfers);
    }

    /**
     * Marks a pending transfer as completed.
     *
     * @param transfer The pending transfer to be marked as completed.
     */
    public void markTransferAsCompleted(Transfer transfer) {
        pendingTransfers.remove(transfer);
        completedTransfers.add(transfer);

        propertyChangeSupport.firePropertyChange("pendingTransfers", null, pendingTransfers);
        propertyChangeSupport.firePropertyChange("completedTransfers", null, completedTransfers);
    }

    /**
     * Serializes the group to a file named "group.ser".
     */
    public void serialize() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("group.ser"))) {
            oos.writeObject(instance);
        } catch (IOException e) {
            System.out.println("Nie udało się zserializować grupy");
        }
    }

    /**
     * Deserializes the group from the file named "group.ser".
     *
     * @return The deserialized group.
     */
    public static Group deserialize() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("group.ser"))) {
            return (Group) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Nie udało się zdeserializować grupy");
            return null;
        }
    }

    /**
     * Finds a user in the group by name.
     *
     * @param name The name of the user to find.
     * @return The user with the given name, or null if not found.
     */
    public User findUserByName(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user; // Found the user with the given name
            }
        }
        return null; // User not found
    }

    /**
     * Adds a property change listener to the group.
     *
     * @param listener The listener to be added.
     */
    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Creates dummy data for testing and presentation purposes.
     */
    public void createDummyData() {
        users.add(new User("Bronisław"));
        users.add(new User("Stanisław"));
        users.add(new User("Radosław"));
        users.add(new User("Władysław"));
        users.add(new User("Krasnystaw"));

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

        markTransferAsCompleted(pendingTransfers.get(0));
    }
}
