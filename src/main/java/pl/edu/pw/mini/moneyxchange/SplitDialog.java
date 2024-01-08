package pl.edu.pw.mini.moneyxchange;

import pl.edu.pw.mini.moneyxchange.data.Division;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.utils.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Stream;

// todo: moze w przyszłosci zrobić z tego część okienka, zamiast nowego
class SplitDialog extends JDialog {
    enum DivisionType {
        EQUAL, EXACT, PERCENTAGE, SHARES
    }

    private DivisionType divisionType;
    private final JPanel dialogPanel;
    private final JPanel inputPanel;
    private final JPanel leftAmountPanel;
    private final JComboBox<String> divisionTypeComboBox;
    // można to załatwić jedną haszmapą, ale tak jest imo czytelniej:
    private final HashMap<User, Double> textFieldInputs;

    private HashMap<User, Double> outputHashMap;

    public HashMap<User, Double> getOutputHashMap() {
        return outputHashMap;
    }

    private final HashSet<User> equalSplitHashSet;
    private final ArrayList<User> users;
    private final double expenseAmount;
    private double parsedAmount;
    private double leftAmount;
    private boolean resultOK;

    public SplitDialog(ArrayList<User> users, double amount) {
        resultOK = false;
        this.users = users;
        this.expenseAmount = amount;
        divisionType = DivisionType.EQUAL;
        textFieldInputs = new HashMap<>();
        outputHashMap = new HashMap<>();
        equalSplitHashSet = new HashSet<>();
        leftAmount = expenseAmount;

        dialogPanel = new JPanel(new GridLayout());
        dialogPanel.setLayout(new GridLayout(0, 1, 10, 10));
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
        gbc.weighty = 0;
        dialogPanel.add(divisionTypePanel, gbc);

        leftAmountPanel = new JPanel();
        dialogPanel.add(leftAmountPanel);
        drawLeftAmountPanel();

        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setLayout(new GridLayout(0, 1, 10, 10));
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        dialogPanel.add(scrollPane, gbc);
        drawDivisionPanel();

        JButton okButton = new JButton("ok");
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        okButton.addActionListener(e -> {
            resultOK = true;
            calculateSplits();
            dispose();
        });
        dialogPanel.add(okButton, gbc);
    }

    private void drawLeftAmountPanel() {
        leftAmountPanel.removeAll();

        JLabel label = new JLabel("Do podzielenia zostało: " + leftAmount);
        leftAmountPanel.add(label);

        leftAmountPanel.revalidate();
        leftAmountPanel.repaint();
    }

    private void drawDivisionPanel() {
        inputPanel.removeAll();

        for (User user : users) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setLayout(new GridLayout(1, 2));
            JLabel label = new JLabel(user.getName());

            JComponent comp = getSplitInputComponent(user);
            panel.add(label);
            panel.add(comp);
            inputPanel.add(panel);
        }

        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private JComponent getSplitInputComponent(User user) {
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
        if (!validateInput(textField.getText())) {
            textFieldInputs.remove(user);
            textField.setBorder(BorderFactory.createLineBorder(Color.RED));
            return;
        }

        textField.setBorder(BorderFactory.createEmptyBorder());

        if (parsedAmount != 0)
            textFieldInputs.put(user, parsedAmount);
        else
            textFieldInputs.remove(user);

        updateLeftAmount();
    }

    private void calculateSplits() {
        switch (divisionType) {
            case EQUAL -> outputHashMap = Division.splitEqually(equalSplitHashSet, expenseAmount);
            case EXACT -> outputHashMap = Division.splitExactly(textFieldInputs);
            case PERCENTAGE -> outputHashMap = Division.splitByPercentages(textFieldInputs, expenseAmount);
            case SHARES -> outputHashMap = Division.splitByShares(textFieldInputs, expenseAmount);
        }
    }

    private void updateLeftAmount() {
        leftAmount = expenseAmount
                - textFieldInputs.values()
                .stream()
                .reduce(Double::sum)
                .orElse(0.0);

        drawLeftAmountPanel();
    }

    private boolean validateInput(String text) {
        if (text.isEmpty()) {
            parsedAmount = 0;
            return true;
        }

        try {
            parsedAmount = Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            parsedAmount = 0;
            return false;
        }

        if (parsedAmount < 0)
            return false;

        // check if integer
        if (divisionType == DivisionType.SHARES && parsedAmount % 1 != 0)
            return false;

        if (parsedAmount > expenseAmount)
            return false;

        return true;
    }

    public boolean isResultOK() {
        return resultOK;
    }
}
