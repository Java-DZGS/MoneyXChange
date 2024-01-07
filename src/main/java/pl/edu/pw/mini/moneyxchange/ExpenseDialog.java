package pl.edu.pw.mini.moneyxchange;

import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.User;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.stream.Stream;

public class ExpenseDialog extends JDialog {
    private final JTextField titleField;
    private final JTextField dateField;
    private final JTextField amountField;
    private final JTextField payerField;
    private ArrayList<User> selectedUsers;
    private final JComboBox<String> splitTypeComboBox;
    private final JComboBox<String> payerComboBox;
    private final String[] userNames;
    private boolean paymentAdded;

    public ExpenseDialog(Group group) {
        super((JFrame) null, "Add New Payment", true);

        titleField = new JTextField();
        dateField = new JTextField((new Date()).toString());
        amountField = new JTextField();
        payerField = new JTextField();
        selectedUsers = new ArrayList<>();

        userNames = group.getUsers().stream().map(User::getName).toArray(String[]::new);
        payerComboBox = new JComboBox<>(userNames);
//        JList<String> userList = new JList<>(userNames);
//        JScrollPane userListScrollPane = new JScrollPane(userList);
//        JButton selectUsersButton = new JButton("Select Users");

        String[] splitTypes = {"Equally", "Unequally"};
        splitTypeComboBox = new JComboBox<>(splitTypes);

        JButton addButton = new JButton("Add Payment");

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Date:"));
        panel.add(dateField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Paid by:"));
        panel.add(payerComboBox);
        panel.add(new JLabel("Split Type:"));
        panel.add(splitTypeComboBox);
        panel.add(addButton);

        splitTypeComboBox.addActionListener(e -> showUserSplitDialog(group.getUsers()));

        addButton.addActionListener(e -> {
            paymentAdded = true;
            dispose();
        });

        add(panel);
    }

    public JTextField getTitleField() {
        return titleField;
    }

    public JTextField getDateField() {
        return dateField;
    }

    public JTextField getAmountField() {
        return amountField;
    }

    public ArrayList<User> getSelectedUsers() {
        return selectedUsers;
    }

    public String getSplitType() {
        return (String) splitTypeComboBox.getSelectedItem();
    }

    public boolean isExpenseAdded() {
        return paymentAdded;
    }

    private ArrayList<User> showUserSplitDialog(ArrayList<User> users) {
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            amount = 0;
        }

        SplitDialog dialog = new SplitDialog(users, amount);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(300, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.requestFocus();

        //dialog.setModal(true);

        //if (dialog.)

//        ArrayList<User> selectedUsers = new ArrayList<>();
//        for (User user : users) {
//            boolean isSelected = JOptionPane.showConfirmDialog(
//                    null,
//                    "Include " + user.getName() + " in the payment?",
//                    "Select Users",
//                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
//            if (isSelected) {
//                selectedUsers.add(user);
//            }
//        }
//        return selectedUsers;

        return null;
    }
}

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

        // todo: zrobić żeby to wyglądało normalnie

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
        dialogPanel.add(divisionTypePanel);
        divisionTypePanel.add(divisionTypeComboBox);

        divisionPanel = new JPanel(new GridBagLayout());
        divisionPanel.setLayout(new GridLayout(0, 1, 10, 10));
        //divisionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JScrollPane scrollPane = new JScrollPane(divisionPanel);
        dialogPanel.add(scrollPane);
        //divisionPanel.add(scrollPane);

        drawDivisionPanel();

        JButton okButton = new JButton("ok");
        okButton.addActionListener(e -> {
            resultOK = true;
            calculateSplits();
            dispose();
        });
        dialogPanel.add(okButton);
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
                textField.addActionListener(e -> handleDoubleTextFieldValue(textField, user));
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
            case EQUAL -> calculateEqualSplits();
            case EXACT -> calculateExactSplits();
            case PERCENTAGE -> calculatePercentageSplits();
            case SHARES -> calculateSharesSplits();
        }
    }

    private void calculateEqualSplits() {
        int n = equalSplitHashSet.size();
        // todo: handle uneven division like 10 / 3
        double splitAmount = amount / n;
        textFieldInputs.clear();
        for (User user : equalSplitHashSet) {
            textFieldInputs.put(user, splitAmount);
        }
    }

    private void calculateExactSplits() {
        outputHashMap = (HashMap<User, Double>) textFieldInputs.clone();
    }

    private void calculatePercentageSplits() {
        for (Map.Entry<User, Double> entry : textFieldInputs.entrySet()) {
            User user = entry.getKey();
            double percent = entry.getValue();

            outputHashMap.put(user, (percent / 100) * amount);
        }
    }

    private void calculateSharesSplits() {
        // how many total shares
        int n = (int)textFieldInputs.values().stream().mapToInt(Double::intValue).reduce(Integer::sum).orElse(1);

        for (Map.Entry<User, Double> entry : textFieldInputs.entrySet()) {
            User user = entry.getKey();
            int shares = entry.getValue().intValue();

            outputHashMap.put(user, (shares / n) * amount);
        }
    }

    public boolean isResultOK() {
        return resultOK;
    }
}
