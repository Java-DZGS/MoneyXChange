package pl.edu.pw.mini.moneyxchange.dialogs;

import org.javamoney.moneta.Money;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.Transfer;
import pl.edu.pw.mini.moneyxchange.utils.DateLabelFormatter;
import pl.edu.pw.mini.moneyxchange.utils.Format;
import pl.edu.pw.mini.moneyxchange.utils.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.Properties;


public class CompleteTransferDialog extends JDialog {
    private final JFormattedTextField amountField;
    private final JDatePickerImpl datePicker;
    private Money transferAmount;
    private Money parsedAmount;
    private boolean resultOK = true;
    private Group group;
    private Money amount;

    public CompleteTransferDialog(Group group, Transfer transfer) {
        super((JFrame) null, "Wykonaj przelew", true);

        this.group = group;
        this.transferAmount = transfer.getAmount();

        UtilDateModel model = new UtilDateModel();
        model.setValue(new Date());

        // todo: zrobiłem na te properties zmienną w klasie Format ale jest na innym branchu,
        // jak juz będzie zmerge'owany to trzeba dodać 😭
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        amountField = new JFormattedTextField(new Format.MonetaryFormatter());
        handleTransferTextInputChange();
        SwingUtils.addChangeListener(amountField, e -> handleTransferTextInputChange());

        JButton completeButton = new JButton("Wykonaj przelew");

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Adjusted the layout for the date picker
        panel.add(new JLabel("Data:"));
        panel.add(datePicker);
        panel.add(new JLabel("Kwota:"));
        panel.add(amountField);
        panel.add(completeButton);

        add(panel);

        completeButton.addActionListener(e -> {
            if (resultOK) {
                Money enteredAmount = parsedAmount != null ? parsedAmount : Money.zero(transferAmount.getCurrency());

                if (enteredAmount.isEqualTo(transferAmount)) {
                    transfer.setDate(getSelectedDate());
                    group.markTransferAsCompleted(transfer);
                } else {
                    Transfer newTransfer = new Transfer(getSelectedDate(), enteredAmount, transfer.getFromUser(), transfer.getToUser());
                    group.addCompletedTransfer(newTransfer);

                    Money remainingAmount = transferAmount.subtract(enteredAmount);
                    transfer.setAmount(remainingAmount);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        null, "Błędne dane", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        this.setSize(300, 200);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void handleTransferTextInputChange() {
        parsedAmount = (Money) amountField.getValue();

        try {
            amountField.commitEdit();
            amountField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        } catch (Exception ex) {
            amountField.setBorder(BorderFactory.createLineBorder(Color.RED));
            resultOK = false;
            return;
        }

        if (validateAmount()) {
            amountField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            resultOK = true;
        } else {
            amountField.setBorder(BorderFactory.createLineBorder(Color.RED));
            resultOK = false;
        }
    }

    private boolean validateAmount() {
        if (parsedAmount == null)
            return false;

        if (parsedAmount.signum() < 0)
            return false;

        if (parsedAmount.isGreaterThan(transferAmount))
            return false;

        return true;
    }

    private Date getSelectedDate() {
        Date selectedDate = (Date) datePicker.getModel().getValue();
        return selectedDate;
    }

}

