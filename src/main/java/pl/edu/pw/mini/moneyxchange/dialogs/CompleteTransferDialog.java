package pl.edu.pw.mini.moneyxchange.dialogs;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.Transfer;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CompleteTransferDialog extends JDialog {
    private final JTextField titleField;
    private final JTextField dateField;
    private final JTextField amountField;
    private final Group group;
    private Money amount;
    public CompleteTransferDialog(Group group, Transfer transfer) {
        super((JFrame) null, "Wykonaj przelew", true);

        this.group = group;

        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(today);

        titleField = new JTextField(transfer.getTitle());
        titleField.setEditable(false);
        dateField = new JTextField(formattedDate);
        amountField = new JTextField(Format.MONETARY_FORMAT.format(transfer.getAmount()));

        JButton completeButton = new JButton("Wykonaj przelew");

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        panel.add(new JLabel("Tytuł:"));
        panel.add(titleField);
        panel.add(new JLabel("Data:"));
        panel.add(dateField);
        panel.add(new JLabel("Kwota:"));
        panel.add(amountField);
        panel.add(completeButton);

        add(panel);

        completeButton.addActionListener(e -> {
            group.markTransferAsCompleted(transfer);
            dispose();
        });

        this.setSize(300, 200);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

}
