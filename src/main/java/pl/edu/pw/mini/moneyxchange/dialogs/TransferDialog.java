package pl.edu.pw.mini.moneyxchange.dialogs;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.Transfer;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransferDialog extends JDialog {
    private final JTextField titleField;
    private final JTextField dateField;
    private final JTextField amountField;
    private final JTextField fromUserField;
    private final JTextField toUserField;
    private final JComboBox<String> toUserComboBox;
    private final JComboBox<String> fromUserComboBox;


    public TransferDialog(Group group) {
        super((JFrame) null, "Add New Payment", true);

        titleField = new JTextField();
        dateField = new JTextField(Format.SIMPLE_DATE_FORMAT.format(new Date()));
        amountField = new JTextField();
        fromUserField = new JTextField();
        toUserField = new JTextField();

        fromUserComboBox = new JComboBox<>(group.getUsers().stream()
                .map(User::getName)
                .toArray(String[]::new));
        toUserComboBox = new JComboBox<>(group.getUsers().stream()
                .map(User::getName)
                .toArray(String[]::new));

        JButton addButton = new JButton("Add Transfer");

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Date:"));
        panel.add(dateField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Select payer:"));
        panel.add(fromUserComboBox);
        panel.add(new JLabel("Select receiver:"));
        panel.add(toUserComboBox);
        panel.add(addButton);

        addButton.addActionListener(e -> {
            String title = titleField.getText();
            String dateString = dateField.getText();
            Money amount = Money.of(Double.parseDouble(amountField.getText()), Format.CURRENCY);
            String fromUserName = (String) fromUserComboBox.getSelectedItem();
            String toUserName = (String) toUserComboBox.getSelectedItem();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            try {
                date = dateFormat.parse(dateString);
            } catch (ParseException ex) {
                //TODO: Obsługa błędu parsowania daty
                ex.printStackTrace();
                return;
            }

            User fromUser = Group.getInstance().findUserByName(fromUserName);
            User toUser = Group.getInstance().findUserByName(toUserName);

            Transfer transfer = new Transfer(title, date, amount, fromUser, toUser);
            Group.getInstance().addCompletedTransfer(transfer);
            fromUser.addCompletedTransfer(transfer);
            dispose();
        });

        setSize(300, 200);
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

    public JTextField getFromUserField() {
        return fromUserField;
    }

    public JTextField getToUserField() {
        return toUserField;
    }

    public JComboBox<String> getFromUserComboBox() {
        return fromUserComboBox;
    }

    public JComboBox<String> getToUserComboBox() {
        return toUserComboBox;
    }
}
