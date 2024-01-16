package pl.edu.pw.mini.moneyxchange.dialogs;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.*;
import pl.edu.pw.mini.moneyxchange.utils.Format;
import pl.edu.pw.mini.moneyxchange.utils.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

public class ExpenseDialog extends JDialog {
    private final JTextField titleField;
    private final JTextField dateField;
    private final JTextField amountField;
    //private final JComboBox<String> splitTypeComboBox;
    private final JComboBox<String> payerComboBox;

    private final Group group;
    private Money amount;
    private Map<User, Money> debtsMap;
    private final String[] userNames;
    private boolean paymentAdded;
    private boolean splitTypeSet;

    public ExpenseDialog(Group group) {
        super((JFrame) null, "Dodaj nowy wydatek", true);

        this.group = group;
        debtsMap = new HashMap<>();

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
                debtsMap = Division.splitEqually(new HashSet<>(group.getUsers()), amount);
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
                    debtsMap,
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
            amount = Money.of(Double.parseDouble(amountField.getText()), Format.CURRENCY);
        } catch (NumberFormatException e) {
            amount = Money.zero(Format.CURRENCY);
        }
    }

    private void showUserSplitDialog(List<User> users) {
        parseAmount();

        SplitDialog dialog = new SplitDialog(users, amount);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(300, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.isResultOK()) {
            splitTypeSet = true;
            debtsMap = dialog.getOutputMap();
        }
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
    private final Map<User, Double> textFieldInputs;

    private Map<User, Money> outputMap;

    public Map<User, Money> getOutputMap() {
        return outputMap;
    }

    private final Set<User> equalSplitSet;
    private final List<User> users;
    private final Money amount;
    private boolean resultOK;

    public SplitDialog(List<User> users, Money amount) {
        resultOK = false;
        this.users = users;
        this.amount = amount;
        divisionType = DivisionType.EQUAL;
        textFieldInputs = new HashMap<>();
        outputMap = new HashMap<>();
        equalSplitSet = new HashSet<>();

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
        if (Objects.requireNonNull(divisionType) == DivisionType.EQUAL) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(true);
            equalSplitSet.add(user);
            checkBox.addActionListener(e -> {
                if (checkBox.isSelected())
                    equalSplitSet.add(user);
                else
                    equalSplitSet.remove(user);
            });

            return checkBox;
        }

        JTextField textField = new JTextField();
        handleDoubleTextFieldValue(textField, user);
        SwingUtils.addChangeListener(textField, e -> handleDoubleTextFieldValue(textField, user));
        return textField;
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
            case EQUAL -> outputMap = Division.splitEqually(equalSplitSet, amount);
            case EXACT -> outputMap = Division.splitExactly(textFieldInputs);
            case PERCENTAGE -> outputMap = Division.splitByPercentages(textFieldInputs, amount);
            case SHARES -> outputMap = Division.splitByShares(textFieldInputs, amount);
        }
    }


    public boolean isResultOK() {
        return resultOK;
    }
}
