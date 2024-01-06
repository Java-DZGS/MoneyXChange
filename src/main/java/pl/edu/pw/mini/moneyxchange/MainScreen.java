package pl.edu.pw.mini.moneyxchange;

import pl.edu.pw.mini.moneyxchange.data.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MainScreen extends JPanel {
    private Group group;

    private final JPanel actionsPanel;
    private final JList<String> userList;

    public MainScreen() {
        group = Group.getInstance();

        JLabel groupNameLabel = new JLabel(group.getName());
        groupNameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton changeNameButton = new JButton("Zmień nazwę grupy");
        JButton serializeButton = new JButton("Serializuj grupę do pliku");
        JButton deserializeButton = new JButton("Deserializuj grupę z pliku");

        actionsPanel = new JPanel(new GridBagLayout());
        actionsPanel.setLayout(new GridLayout(0, 1));
        JScrollPane transfersScrollPane = new JScrollPane(actionsPanel);
        importActions();

        JButton addPaymentButton = new JButton("Dodaj nową płatność");

        userList = new JList<>(group.getUsers().stream().map(User::getName).toArray(String[]::new));
        JScrollPane userListScrollPane = new JScrollPane(userList);

        int padding = 10;
        setBorder(new EmptyBorder(padding, padding, padding, padding));
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(new EmptyBorder(0, 0, padding, 0));
        topPanel.add(groupNameLabel);
        topPanel.add(changeNameButton);
        topPanel.add(serializeButton);
        topPanel.add(deserializeButton);

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(new JLabel("Historia akcji"), BorderLayout.NORTH);
        historyPanel.add(transfersScrollPane, BorderLayout.CENTER);
        historyPanel.add(addPaymentButton, BorderLayout.SOUTH);

        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.add(new JLabel("Lista członków grupy"), BorderLayout.NORTH);
        usersPanel.add(userListScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, historyPanel, usersPanel);
        splitPane.setResizeWeight(0.5);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // BUTTONS
        changeNameButton.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog("Wprowadź nową nazwę grupy:");
            group.setName(newName);
            groupNameLabel.setText("Nazwa grupy: " + group.getName());
        });

        serializeButton.addActionListener(e -> {
            group.serialize();
            JOptionPane.showMessageDialog(null, "Grupa zserializowana do pliku.");
        });

        deserializeButton.addActionListener(e -> {
            group = Group.deserialize();
            assert group != null;
            groupNameLabel.setText(group.getName());
            userList.setListData(group.getUsers().stream().map(User::getName).toArray(String[]::new));
            JOptionPane.showMessageDialog(null, "Grupa zdeserializowana z pliku.");
        });

        addPaymentButton.addActionListener(e -> {
            // TODO
            showPaymentDialog(group);
        });
    }

    private void showPaymentDialog(Group group) {
        ExpenseDialog dialog = new ExpenseDialog(group);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (dialog.isExpenseAdded()) {
            try {
                Expense expense = new Expense(
                        new User("name", 1), //temp
                        Double.parseDouble(dialog.getAmountField().getText()),
                        new HashMap<>(), // temp
                        dialog.getTitleField().getText(),
                        dateFormat.parse(dialog.getDateField().getText()),
                        ExpenseCategory.OTHER //temp
                );
                actionsPanel.add(createExpensePanel(expense), getActionPanelGbc());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private JPanel createTransferPanel(Transfer transfer) {
        JPanel transferPanel = new JPanel();
        transferPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        transferPanel.setLayout(new GridLayout(3, 1));

        JLabel titleLabel = new JLabel("Przelew od " + transfer.getFromUser() + " do " + transfer.getToUser());
        JLabel dateLabel = new JLabel("Data: " + transfer.getDate());
        JLabel amountLabel = new JLabel("Kwota: " + transfer.getAmount());

        transferPanel.add(titleLabel);
        transferPanel.add(dateLabel);
        transferPanel.add(amountLabel);

        return transferPanel;
    }

    private JPanel createExpensePanel(Expense expense) {
        JPanel expensePanel = new JPanel();
        expensePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        expensePanel.setLayout(new GridLayout(4, 1));

        JLabel titleLabel = new JLabel("Tytuł: " + expense.getName());
        JLabel dateLabel = new JLabel("Data: " + expense.getDate());
        JLabel amountLabel = new JLabel("Kwota: " + expense.getAmount());
        JLabel usersLabel = new JLabel("Użytkownicy: " + String.join(", ",
                expense.getParticipants().stream().map(User::getName).toArray(String[]::new)));

        expensePanel.add(titleLabel);
        expensePanel.add(dateLabel);
        expensePanel.add(amountLabel);
        expensePanel.add(usersLabel);

        return expensePanel;
    }

    private GridBagConstraints getActionPanelGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 100;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.PAGE_START;
        return gbc;
    }

    private void importActions() {
        ArrayList<MoneyAction> actionsList = group.getActionsList();

        for (MoneyAction action : actionsList) {
            if (action instanceof Expense) {
                actionsPanel.add(createExpensePanel((Expense) action), getActionPanelGbc());
            } else if (action instanceof Transfer) {
                actionsPanel.add(createTransferPanel((Transfer) action), getActionPanelGbc());
            }
        }
    }
}
