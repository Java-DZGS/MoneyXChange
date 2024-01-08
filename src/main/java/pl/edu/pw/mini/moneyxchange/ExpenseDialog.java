package pl.edu.pw.mini.moneyxchange;

import pl.edu.pw.mini.moneyxchange.data.*;
import pl.edu.pw.mini.moneyxchange.utils.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpenseDialog extends JDialog {
    private final JTextField titleField;
    private final JTextField dateField;
    private final JTextField amountField;
    //private final JComboBox<String> splitTypeComboBox;
    private final JComboBox<String> payerComboBox;

    private final Group group;
    private double amount;
    private HashMap<User, Double> debtsHashMap;
    private final String[] userNames;
    private boolean paymentAdded;
    private boolean splitTypeSet;

    public ExpenseDialog(Group group) {
        super((JFrame) null, "Dodaj nowy wydatek", true);

        this.group = group;
        debtsHashMap = new HashMap<>();

        titleField = new JTextField();
        dateField = new JTextField("2022-12-20");//(new Date()).toString());
        amountField = new JTextField("20");
        userNames = group.getUsers().stream().map(User::getName).toArray(String[]::new);
        payerComboBox = new JComboBox<>(userNames);

        JButton splitButton = new JButton("Podziel wydatek");
        JButton addButton = new JButton("Dodaj wydatek");

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        panel.add(new JLabel("Tytuł:"));
        panel.add(titleField);
        panel.add(new JLabel("Data:"));
        panel.add(dateField);
        panel.add(new JLabel("Kwota:"));
        panel.add(amountField);
        panel.add(new JLabel("Zapłacone przez:"));
        panel.add(payerComboBox);
        panel.add(splitButton);
        panel.add(addButton);

        add(panel);

        splitButton.addActionListener(e -> {
            showUserSplitDialog(group.getUsers());
        });

        addButton.addActionListener(e -> {
            if (!splitTypeSet) {
                parseAmount();
                debtsHashMap = Division.splitEqually(new HashSet<>(group.getUsers()), amount);
            }
            paymentAdded = true;
            dispose();
        });

    }

    public Expense getExpense() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return new Expense(
                    group.findUserByName(Objects.requireNonNull(payerComboBox.getSelectedItem()).toString()),
                    amount,
                    debtsHashMap,
                    titleField.getText(),
                    dateFormat.parse(dateField.getText()),
                    ExpenseCategory.OTHER // todo
            );
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isExpenseAdded() {
        return paymentAdded;
    }

    private void parseAmount() {
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            amount = 0;
        }
    }

    private ArrayList<User> showUserSplitDialog(ArrayList<User> users) {
        parseAmount();

        SplitDialog dialog = new SplitDialog(users, amount);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(300, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.isResultOK()) {
            splitTypeSet = true;
            debtsHashMap = dialog.getOutputHashMap();
        }

        return null;
    }
}
