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

// todo: moze w przyszłosci zrobić z tego część okienka, zamiast nowego
class SplitDialog extends JDialog {
    enum DivisionType {
        EQUAL, EXACT, PERCENTAGE, SHARES
    }

    private DivisionType divisionType;
    private final JPanel dialogPanel;
    private final JPanel divisionPanel;
    private final JComboBox<String> divisionTypeComboBox;
    // można to załatwić jedną haszmapą, ale tak jest imo czytelniej:
    private final HashMap<User, Double> textFieldInputs;

    private HashMap<User, Double> outputHashMap;

    public HashMap<User, Double> getOutputHashMap() {
        return outputHashMap;
    }

    private final HashSet<User> equalSplitHashSet;
    private final ArrayList<User> users;
    private final double amount;
    private boolean resultOK;

    public SplitDialog(ArrayList<User> users, double amount) {
        resultOK = false;
        this.users = users;
        this.amount = amount;
        divisionType = DivisionType.EQUAL;
        textFieldInputs = new HashMap<>();
        outputHashMap = new HashMap<>();
        equalSplitHashSet = new HashSet<>();

        dialogPanel = new JPanel(new GridLayout());
        dialogPanel.setLayout(new GridLayout(3, 1, 10, 10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        add(dialogPanel);

        divisionTypeComboBox = new JComboBox<>(
                // convert enum values to string array
                Stream.of(DivisionType.values())
                        .map(DivisionType::name)
                        .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()) // capitalize only first letter
                        .toArray(String[]::new));

        divisionTypeComboBox.addActionListener(e -> {
            divisionType = DivisionType.valueOf(
                    Objects.requireNonNull(divisionTypeComboBox
                                    .getSelectedItem())
                            .toString()
                            .toUpperCase());
            drawDivisionPanel();
        });

        JPanel divisionTypePanel = new JPanel();
        divisionTypePanel.add(divisionTypeComboBox);

        // todo: te gbc poprawić
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.1;
        dialogPanel.add(divisionTypePanel, gbc);

        divisionPanel = new JPanel(new GridBagLayout());
        divisionPanel.setLayout(new GridLayout(0, 1, 10, 10));
        JScrollPane scrollPane = new JScrollPane(divisionPanel);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 3.0;
        dialogPanel.add(scrollPane, gbc);

        drawDivisionPanel();

        JButton okButton = new JButton("ok");
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.1;
        okButton.addActionListener(e -> {
            resultOK = true;
            calculateSplits();
            dispose();
        });
        dialogPanel.add(okButton, gbc);
    }

    private void drawDivisionPanel() {
        divisionPanel.removeAll();

        for (User user : users) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setLayout(new GridLayout(1, 2));
            JLabel label = new JLabel(user.getName());

            JComponent comp = getSplitField(user);
            panel.add(label);
            panel.add(comp);
            divisionPanel.add(panel);
        }

        divisionPanel.revalidate();
        divisionPanel.repaint();
    }

    private JComponent getSplitField(User user) {
        switch (divisionType) {
            case EQUAL -> {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected(true);
                equalSplitHashSet.add(user);
                checkBox.addActionListener(e -> {
                    if (checkBox.isSelected())
                        equalSplitHashSet.add(user);
                    else
                        equalSplitHashSet.remove(user);
                });
                return checkBox;
            }
            default -> {
                JTextField textField = new JTextField();
                handleDoubleTextFieldValue(textField, user);
                SwingUtils.addChangeListener(textField, e -> handleDoubleTextFieldValue(textField, user));
                return textField;
            }
        }
    }

    private void handleDoubleTextFieldValue(JTextField textField, User user) {
        // todo: handle invalid values:
        // values larger than amount, values lesser than 0, non-integers in shares
        double amount;

        try {
            amount = Double.parseDouble(textField.getText());
        } catch (NumberFormatException ex) {
            amount = 0;
        }

        if (amount != 0)
            textFieldInputs.put(user, amount);
        else
            textFieldInputs.remove(user);
    }

    private void calculateSplits() {
        switch (divisionType) {
            case EQUAL -> outputHashMap = Division.splitEqually(equalSplitHashSet, amount);
            case EXACT -> outputHashMap = Division.splitExactly(textFieldInputs);
            case PERCENTAGE -> outputHashMap = Division.splitByPercentages(textFieldInputs, amount);
            case SHARES -> outputHashMap = Division.splitByShares(textFieldInputs, amount);
        }
    }


    public boolean isResultOK() {
        return resultOK;
    }
}
