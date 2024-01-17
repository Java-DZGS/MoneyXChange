package pl.edu.pw.mini.moneyxchange.dialogs;

import org.javamoney.moneta.Money;
//import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import pl.edu.pw.mini.moneyxchange.data.*;
import pl.edu.pw.mini.moneyxchange.utils.Format;
import pl.edu.pw.mini.moneyxchange.utils.SwingUtils;
import pl.edu.pw.mini.moneyxchange.utils.splitters.EqualSplitter;

import javax.money.MonetaryException;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class ExpenseDialog extends JDialog {
    private final JTextField titleField;
    private final JDatePickerImpl datePicker;
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

        UtilDateModel model = new UtilDateModel();
        model.setValue(new Date());
        Properties p = new Properties();
        p.put("text.today", "Dzisiaj");
        p.put("text.month", "Miesiąc");
        p.put("text.year", "Rok");

        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new Format.DateLabelFormatter());//Format.DATE_LABEL_FORMATTER);

        amountField = new JTextField();
        userNames = group.getUsers().stream().map(User::getName).toArray(String[]::new);
        payerComboBox = new JComboBox<>(userNames);

        JButton splitButton = new JButton("Podziel wydatek");
        JButton addButton = new JButton("Dodaj wydatek");

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        panel.add(new JLabel("Tytuł:"));
        panel.add(titleField);
        panel.add(new JLabel("Data:"));
        panel.add(datePicker);
        panel.add(new JLabel("Kwota:"));
        panel.add(amountField);
        panel.add(new JLabel("Zapłacone przez:"));
        panel.add(payerComboBox);
        panel.add(splitButton);
        panel.add(addButton);

        add(panel);

        SwingUtils.addChangeListener(amountField, e -> parseAmount());

        splitButton.addActionListener(e -> showUserSplitDialog(group.getUsers()));

        addButton.addActionListener(e -> {
            if (!isDataSet())
                return;

            if (!splitTypeSet) {
                parseAmount();
                EqualSplitter splitter = new EqualSplitter(amount);
                for (User user : group.getUsers()) {
                    splitter.addUser(user, "");
                }
                debtsMap = splitter.split();
            }
            paymentAdded = true;
            dispose();
        });

    }

    private boolean isDataSet()
    {
        if (amount == null || amount.isNegativeOrZero())
        {
            JOptionPane.showMessageDialog(
                    null, "Podaj wartość wydatku większą od 0", "Błąd", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (titleField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    null, "Podaj tytuł wydatku", "Błąd", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public Expense getExpense() {
        return new Expense(
                group.findUserByName(Objects.requireNonNull(payerComboBox.getSelectedItem()).toString()),
                amount,
                debtsMap,
                titleField.getText(),
                (Date) datePicker.getModel().getValue(),
                ExpenseCategory.OTHER // todo
        );

    }

    public boolean isExpenseAdded() {
        return paymentAdded;
    }

    private void parseAmount() {
        try {
            amount = Money.of(Double.parseDouble(amountField.getText()), Format.CURRENCY);
        } catch (MonetaryException e) {
            amount = Money.zero(Format.CURRENCY);
        }
    }

    private void showUserSplitDialog(List<User> users) {
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

