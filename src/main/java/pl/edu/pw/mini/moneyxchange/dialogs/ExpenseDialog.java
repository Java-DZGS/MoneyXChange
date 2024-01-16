package pl.edu.pw.mini.moneyxchange.dialogs;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.*;
import pl.edu.pw.mini.moneyxchange.data.divisions.*;
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
        dateField = new JTextField(Format.SIMPLE_DATE_FORMAT.format(new Date()));
        amountField = new JTextField("20");
        userNames = group.getUsers().stream().map(User::getName).toArray(String[]::new);
        payerComboBox = new JComboBox<>(userNames);

        JButton splitButton = new JButton("Podziel wydatek");
        JButton addButton = new JButton("Dodaj wydatek");

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
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

        splitButton.addActionListener(e -> showUserSplitDialog(group.getUsers()));

        addButton.addActionListener(e -> {
            if (!splitTypeSet) {
                parseAmount();
                debtsMap = new HashMap<>(); // todo
                //debtsMap = //Division.splitEqually(new HashSet<>(group.getUsers()), amount);
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
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.isResultOK()) {
            splitTypeSet = true;
            debtsMap = dialog.getOutputMap();
        }
    }
}

