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
import java.util.stream.Stream;

public class ExpenseDialog extends JDialog {
    private final JTextField titleField;
    private final JDatePickerImpl datePicker;
    private final JTextField amountField;
    private boolean amountValidationOK;
    private final JComboBox<String> payerComboBox;
    private final JComboBox<String> categoryComboBox;
    private Money amount;
    private Map<User, Money> debtsMap;
    private final String[] userNames;
    private boolean paymentAdded;
    private boolean splitTypeSet;

    public ExpenseDialog() {
        super((Frame) null, "Dodaj nowy wydatek", true);

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
        userNames = Group.getInstance().getUsers().stream().map(User::getName).toArray(String[]::new);
        payerComboBox = new JComboBox<>(userNames);
        String[] categories = Stream.of(ExpenseCategory.values())
                .map(ExpenseCategory::name)
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()) // capitalize only first letter
                .toArray(String[]::new);
        categoryComboBox = new JComboBox<>(categories);

        JButton splitButton = new JButton("Podziel wydatek");
        JButton addButton = new JButton("Dodaj wydatek");

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        panel.add(new JLabel("Tytuł:"));
        panel.add(titleField);
        panel.add(new JLabel("Data:"));
        panel.add(datePicker);
        panel.add(new JLabel("Kwota:"));
        panel.add(amountField);
        panel.add(new JLabel("Zapłacone przez:"));
        panel.add(payerComboBox);
        panel.add(new JLabel("Kategoria:"));
        panel.add(categoryComboBox);
        panel.add(splitButton);
        panel.add(addButton);

        add(panel);

        SwingUtils.addChangeListener(amountField, e -> handleAmountFieldTextChange());

        splitButton.addActionListener(e -> {
            if (!amountValidationOK) {
                JOptionPane.showMessageDialog(
                        null, "Podaj poprawną kwotę wydatku", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            showUserSplitDialog(Group.getInstance().getUsers());
        });

        addButton.addActionListener(e -> {
            if (!isDataSet())
                return;

            if (!splitTypeSet) {
                handleAmountFieldTextChange();
                EqualSplitter splitter = new EqualSplitter(amount);
                for (User user : Group.getInstance().getUsers()) {
                    splitter.addUser(user, "");
                }
                debtsMap = splitter.split();
            }

            Expense newExpense = new Expense(Group.getInstance().findUserByName(Objects.requireNonNull(payerComboBox.getSelectedItem()).toString()),
                    amount, debtsMap, titleField.getText(), (Date) datePicker.getModel().getValue(),
                    ExpenseCategory.valueOf(
                            ((String) Objects.requireNonNull(categoryComboBox.getSelectedItem())).toUpperCase()
                    )
            );

            Group.getInstance().addExpense(newExpense);

            dispose();
        });
    }

    public ExpenseDialog(User payer) {
        this();

        payerComboBox.setSelectedItem(payer.getName());
        payerComboBox.revalidate();
        payerComboBox.repaint();
    }

    private boolean isDataSet() {
        if (!amountValidationOK) {
            JOptionPane.showMessageDialog(
                    null, "Podaj poprawną kwotę wydatku", "Błąd", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (amount == null || amount.isNegativeOrZero()) {
            JOptionPane.showMessageDialog(
                    null, "Podaj kwotę wydatku większą od 0", "Błąd", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (titleField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    null, "Podaj tytuł wydatku", "Błąd", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void handleAmountFieldTextChange() {
        try {
            // todo: obsługiwanie różnych walut?
            amount = Money.of(Double.parseDouble(amountField.getText()), Format.CURRENCY);
            amountValidationOK = true;
        } catch (MonetaryException | NumberFormatException e) {
            amount = Money.zero(Format.CURRENCY);
            amountValidationOK = false;
        }

        if (amountValidationOK)
            amountField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        else
            amountField.setBorder(BorderFactory.createLineBorder(Color.RED));
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

