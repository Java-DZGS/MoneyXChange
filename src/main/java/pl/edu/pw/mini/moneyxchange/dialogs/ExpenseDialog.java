package pl.edu.pw.mini.moneyxchange.dialogs;

import org.javamoney.moneta.Money;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import pl.edu.pw.mini.moneyxchange.data.Expense;
import pl.edu.pw.mini.moneyxchange.data.ExpenseCategory;
import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.utils.Format;
import pl.edu.pw.mini.moneyxchange.utils.SwingUtils;
import pl.edu.pw.mini.moneyxchange.utils.splitters.EqualSplitter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class ExpenseDialog extends JDialog {
    private final JTextField titleField;
    private final JDatePickerImpl datePicker;
    private final JFormattedTextField amountField;
    private boolean amountValidationOK;
    private final JComboBox<User> payerComboBox;
    private final JComboBox<ExpenseCategory> categoryComboBox;
    private Money amount;
    private Map<User, Money> debtsMap;
    private boolean splitTypeSet;

    public ExpenseDialog() {
        super((Frame) null, "Dodaj nowy wydatek", true);

        debtsMap = new HashMap<>();

        titleField = new JTextField();

        UtilDateModel model = new UtilDateModel();
        model.setValue(new Date());
        JDatePanelImpl datePanel = new JDatePanelImpl(model, Format.DATE_PICKER_PROPERTIES);
        datePicker = new JDatePickerImpl(datePanel, Format.DATE_LABEL_FORMATTER);

        amountField = new JFormattedTextField(new Format.MonetaryFormatter());
        payerComboBox = new JComboBox<>(Group.getInstance().getUsers().toArray(User[]::new));
        categoryComboBox = new JComboBox<>(ExpenseCategory.values());
        categoryComboBox.setSelectedItem(ExpenseCategory.OTHER);

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

            Expense newExpense = new Expense((User) Objects.requireNonNull(payerComboBox.getSelectedItem()),
                    amount, debtsMap, titleField.getText(), (Date) datePicker.getModel().getValue(),
                    (ExpenseCategory) categoryComboBox.getSelectedItem()
            );

            Group.getInstance().addExpense(newExpense);

            dispose();
        });
    }

    public ExpenseDialog(User payer) {
        this();

        payerComboBox.setSelectedItem(payer);
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
            amountField.commitEdit();
            amountField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            amountValidationOK = true;
        } catch (Exception ex) {
            amountField.setBorder(BorderFactory.createLineBorder(Color.RED));
            amountValidationOK = false;
        }

        amount = (Money) amountField.getValue();
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

