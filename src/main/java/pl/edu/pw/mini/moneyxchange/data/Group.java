package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.imageio.ImageIO;
import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.DateFormat;
import java.text.Normalizer;
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
    private transient SwingPropertyChangeSupport propertyChangeSupport;

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
        expenses.sort(Comparator.comparing(Expense::getDate).reversed());
        propertyChangeSupport.firePropertyChange("expenses", null, expenses);
        propertyChangeSupport.firePropertyChange("action", null, expense.getPanel());
    }

    /**
     * Adds a new pending transfer to the group.
     *
     * @param transfer The pending transfer to be added.
     */
    public void addPendingTransfer(Transfer transfer) {
        pendingTransfers.add(transfer);
        //TODO: not working
//        pendingTransfers = minTransfers(pendingTransfers);
        propertyChangeSupport.firePropertyChange("pendingTransfers", null, pendingTransfers);
    }

    /**
     * Adds a new completed transfer to the group.
     *
     * @param transfer The completed transfer to be added.
     */
    public void addCompletedTransfer(Transfer transfer) {
        completedTransfers.add(transfer);
        completedTransfers.sort(Comparator.comparing(Transfer::getDate).reversed());
        propertyChangeSupport.firePropertyChange("completedTransfers", null, completedTransfers);
        propertyChangeSupport.firePropertyChange("action", null, transfer.getPanel());
    }

    /**
     * Marks a pending transfer as completed.
     *
     * @param transfer The pending transfer to be marked as completed.
     */
    public void markTransferAsCompleted(Transfer transfer) {
        pendingTransfers.remove(transfer);
        completedTransfers.add(transfer);

        completedTransfers.sort(Comparator.comparing(Transfer::getDate).reversed());

        propertyChangeSupport.firePropertyChange("pendingTransfers", null, pendingTransfers);
        propertyChangeSupport.firePropertyChange("completedTransfers", null, completedTransfers);
        propertyChangeSupport.firePropertyChange("action", null, transfer.getPanel());
    }

    /**
     * Serializes the group to a file named "group.ser".
     *
     * @throws IOException Any exception thrown by the underlying OutputStream.
     */
    public void serialize() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("group.ser"))) {
            oos.writeObject(instance);
        }
    }

    /**
     * Deserializes the group from the file named "group.ser".
     *
     * @throws IOException Any exception thrown by the underlying OutputStream.
     * @throws ClassNotFoundException Class of Group cannot be found.
     */
    public static void deserialize() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("group.ser"))) {
            Group deserialized = (Group) ois.readObject();
            deserialized.propertyChangeSupport = instance.propertyChangeSupport;
            instance = deserialized;

            instance.propertyChangeSupport.firePropertyChange("users", null, instance.users);
            instance.propertyChangeSupport.firePropertyChange("expenses", null, instance.expenses);
            instance.propertyChangeSupport.firePropertyChange("pendingTransfers", null, instance.pendingTransfers);
            instance.propertyChangeSupport.firePropertyChange("completedTransfers", null, instance.completedTransfers);
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
        addUser(new User("Bronisław"));
        addUser(new User("Stanisław"));
        addUser(new User("Radosław"));
        addUser(new User("Władysław"));
        addUser(new User("Krasnystaw"));

        try {
            File image = new File("test.png");
            users.get(0).setImage(ImageIO.read(image));

            image = new File("test2.png");
            users.get(4).setImage(ImageIO.read(image));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Expense expense1 = new Expense(users.get(1), Money.of(50.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(0.0, Format.CURRENCY));
                        put(users.get(1), Money.of(25.0, Format.CURRENCY));
                        put(users.get(2), Money.of(25.0, Format.CURRENCY));
                        put(users.get(3), Money.of(0.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Pizza", Format.SIMPLE_DATE_FORMAT.parse("2023-01-01"), ExpenseCategory.FOOD
            );
            addExpense(expense1);

            Expense expense2 = new Expense(users.get(4), Money.of(30.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(10.0, Format.CURRENCY));
                        put(users.get(1), Money.of(15.0, Format.CURRENCY));
                        put(users.get(2), Money.of(5.0, Format.CURRENCY));
                        put(users.get(3), Money.of(0.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Uber", Format.SIMPLE_DATE_FORMAT.parse("2023-01-02"), ExpenseCategory.TRANSPORT
            );
            addExpense(expense2);

            Expense expense3 = new Expense(users.get(3), Money.of(45.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(10.0, Format.CURRENCY));
                        put(users.get(1), Money.of(15.0, Format.CURRENCY));
                        put(users.get(2), Money.of(10.0, Format.CURRENCY));
                        put(users.get(3), Money.of(10.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Kino", Format.SIMPLE_DATE_FORMAT.parse("2023-01-03"), ExpenseCategory.ENTERTAINMENT
            );
            addExpense(expense3);

            Expense expense4 = new Expense(users.get(1), Money.of(50.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(0.0, Format.CURRENCY));
                        put(users.get(1), Money.of(25.0, Format.CURRENCY));
                        put(users.get(2), Money.of(25.0, Format.CURRENCY));
                        put(users.get(3), Money.of(0.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Frytki", Format.SIMPLE_DATE_FORMAT.parse("2023-01-01"), ExpenseCategory.FOOD
            );
            addExpense(expense4);

            Expense expense5 = new Expense(users.get(2), Money.of(20.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(8.0, Format.CURRENCY));
                        put(users.get(1), Money.of(6.0, Format.CURRENCY));
                        put(users.get(2), Money.of(4.0, Format.CURRENCY));
                        put(users.get(3), Money.of(2.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Kawa ", Format.SIMPLE_DATE_FORMAT.parse("2023-01-06"), ExpenseCategory.FOOD
            );
            addExpense(expense5);

            Expense expense6 = new Expense(users.get(3), Money.of(25.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(12.0, Format.CURRENCY));
                        put(users.get(1), Money.of(8.0, Format.CURRENCY));
                        put(users.get(2), Money.of(5.0, Format.CURRENCY));
                        put(users.get(3), Money.of(0.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Koncert", Format.SIMPLE_DATE_FORMAT.parse("2023-01-07"), ExpenseCategory.ENTERTAINMENT
            );
            addExpense(expense6);

            Expense expense7 = new Expense(users.get(4), Money.of(40.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(15.0, Format.CURRENCY));
                        put(users.get(1), Money.of(15.0, Format.CURRENCY));
                        put(users.get(2), Money.of(10.0, Format.CURRENCY));
                        put(users.get(3), Money.of(0.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Pociąg", Format.SIMPLE_DATE_FORMAT.parse("2023-01-08"), ExpenseCategory.TRANSPORT
            );
            addExpense(expense7);

            Expense expense8 = new Expense(users.get(0), Money.of(15.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(1), Money.of(5.0, Format.CURRENCY));
                        put(users.get(2), Money.of(5.0, Format.CURRENCY));
                        put(users.get(3), Money.of(5.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Książki", Format.SIMPLE_DATE_FORMAT.parse("2022-08-15"), ExpenseCategory.OTHER//ExpenseCategory.EDUCATION
            );
            addExpense(expense8);

            Expense expense9 = new Expense(users.get(1), Money.of(60.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(20.0, Format.CURRENCY));
                        put(users.get(2), Money.of(20.0, Format.CURRENCY));
                        put(users.get(3), Money.of(20.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Spa Weekend", Format.SIMPLE_DATE_FORMAT.parse("2023-05-20"), ExpenseCategory.OTHER// ExpenseCategory.HEALTH
            );
            addExpense(expense9);

            Expense expense10 = new Expense(users.get(2), Money.of(35.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(10.0, Format.CURRENCY));
                        put(users.get(1), Money.of(15.0, Format.CURRENCY));
                        put(users.get(3), Money.of(10.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Muzeum Sztuki", Format.SIMPLE_DATE_FORMAT.parse("2024-02-10"), ExpenseCategory.ENTERTAINMENT
            );
            addExpense(expense10);

            Expense expense11 = new Expense(users.get(3), Money.of(40.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(15.0, Format.CURRENCY));
                        put(users.get(1), Money.of(15.0, Format.CURRENCY));
                        put(users.get(2), Money.of(10.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Teatr", Format.SIMPLE_DATE_FORMAT.parse("2022-11-12"), ExpenseCategory.ENTERTAINMENT
            );
            addExpense(expense11);

            Expense expense12 = new Expense(users.get(4), Money.of(25.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(10.0, Format.CURRENCY));
                        put(users.get(1), Money.of(10.0, Format.CURRENCY));
                        put(users.get(2), Money.of(5.0, Format.CURRENCY));
                        put(users.get(3), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Sklep Sportowy", Format.SIMPLE_DATE_FORMAT.parse("2023-08-22"), ExpenseCategory.OTHER//CLOTHING
            );
            addExpense(expense12);

            Expense expense13 = new Expense(users.get(1), Money.of(30.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(10.0, Format.CURRENCY));
                        put(users.get(2), Money.of(10.0, Format.CURRENCY));
                        put(users.get(3), Money.of(10.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Park Rozrywki", Format.SIMPLE_DATE_FORMAT.parse("2024-04-05"), ExpenseCategory.ENTERTAINMENT
            );
            addExpense(expense13);

            Expense expense14 = new Expense(users.get(2), Money.of(18.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(8.0, Format.CURRENCY));
                        put(users.get(1), Money.of(5.0, Format.CURRENCY));
                        put(users.get(3), Money.of(5.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Muzyka Online", Format.SIMPLE_DATE_FORMAT.parse("2023-09-15"), ExpenseCategory.ENTERTAINMENT
            );
            addExpense(expense14);

            Expense expense15 = new Expense(users.get(3), Money.of(22.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(10.0, Format.CURRENCY));
                        put(users.get(1), Money.of(8.0, Format.CURRENCY));
                        put(users.get(2), Money.of(4.0, Format.CURRENCY));
                        put(users.get(4), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Łyżwy", Format.SIMPLE_DATE_FORMAT.parse("2024-01-28"), ExpenseCategory.OTHER//SPORTS
            );
            addExpense(expense15);

            Expense expense16 = new Expense(users.get(4), Money.of(28.0, Format.CURRENCY),
                    new HashMap<>() {{
                        put(users.get(0), Money.of(12.0, Format.CURRENCY));
                        put(users.get(1), Money.of(10.0, Format.CURRENCY));
                        put(users.get(2), Money.of(6.0, Format.CURRENCY));
                        put(users.get(3), Money.of(0.0, Format.CURRENCY));
                    }},
                    "Konferencja IT", Format.SIMPLE_DATE_FORMAT.parse("2022-06-17"), ExpenseCategory.OTHER//TECHNOLOGY
            );
            addExpense(expense16);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
