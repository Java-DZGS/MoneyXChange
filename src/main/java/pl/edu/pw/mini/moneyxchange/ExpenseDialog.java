package pl.edu.pw.mini.moneyxchange;

import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ExpenseDialog extends JDialog {
    private final JTextField titleField;
    private final JTextField dateField;
    private final JTextField amountField;
    private ArrayList<User> selectedUsers;
    private final JComboBox<String> splitTypeComboBox;
    private boolean paymentAdded;

    public ExpenseDialog(Group group) {
        super((JFrame) null, "Add New Payment", true);

        titleField = new JTextField();
        dateField = new JTextField();
        amountField = new JTextField();
        selectedUsers = new ArrayList<>();

        String[] userNames = group.getUsers().stream().map(User::getName).toArray(String[]::new);
        JList<String> userList = new JList<>(userNames);
        JScrollPane userListScrollPane = new JScrollPane(userList);
        JButton selectUsersButton = new JButton("Select Users");

        String[] splitTypes = {"Equally", "Percentage"};
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
        panel.add(new JLabel("Select Users:"));
        panel.add(userListScrollPane);
        panel.add(new JLabel("Split Type:"));
        panel.add(splitTypeComboBox);
        panel.add(selectUsersButton);
        panel.add(addButton);

        selectUsersButton.addActionListener(e -> selectedUsers = showUserSelectionDialog(group.getUsers()));

        addButton.addActionListener(e -> {
            paymentAdded = true;
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

    public ArrayList<User> getSelectedUsers() {
        return selectedUsers;
    }

    public String getSplitType() {
        return (String) splitTypeComboBox.getSelectedItem();
    }

    public boolean isExpenseAdded() {
        return paymentAdded;
    }

    private ArrayList<User> showUserSelectionDialog(ArrayList<User> users) {
        ArrayList<User> selectedUsers = new ArrayList<>();
        for (User user : users) {
            boolean isSelected = JOptionPane.showConfirmDialog(
                    null,
                    "Include " + user.getName() + " in the payment?",
                    "Select Users",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            if (isSelected) {
                selectedUsers.add(user);
            }
        }
        return selectedUsers;
    }
}
