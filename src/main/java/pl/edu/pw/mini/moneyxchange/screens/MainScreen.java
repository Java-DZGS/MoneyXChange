package pl.edu.pw.mini.moneyxchange.screens;

import pl.edu.pw.mini.moneyxchange.dialogs.ExpenseDialog;
import pl.edu.pw.mini.moneyxchange.data.Expense;
import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.MoneyAction;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.utils.Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

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

        // TODO: wszystkie akcje, nie tylko dodawanie przelewów
        actionsPanel = new JPanel(new GridBagLayout());

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(0,0));
        actionsPanel.add(spacer, Layout.getGridBagSpacerConstraints());
        JScrollPane transfersScrollPane = new JScrollPane(actionsPanel);
        //importActions();

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
            showPaymentDialog(group);
        });
    }

    private void showPaymentDialog(Group group) {
        ExpenseDialog dialog = new ExpenseDialog(group);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.isExpenseAdded()) {
            Expense expense = dialog.getExpense();
            group.addExpense(expense);
            actionsPanel.add(expense.getPanel(), Layout.getGridBagElementConstraints(), 0);
        }
    }

    private void importActions() {
        List<MoneyAction> actionsList = group.getActionsList();

        for (MoneyAction action : actionsList) {
            actionsPanel.add(action.getPanel(), Layout.getGridBagElementConstraints());
        }
    }
}
