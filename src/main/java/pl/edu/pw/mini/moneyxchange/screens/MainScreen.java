package pl.edu.pw.mini.moneyxchange.screens;

import pl.edu.pw.mini.moneyxchange.data.*;
import pl.edu.pw.mini.moneyxchange.dialogs.ExpenseDialog;
import pl.edu.pw.mini.moneyxchange.utils.Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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

// Create a list to hold both Transfer and Expense objects
        List<SortablePanel> sortedPanels = new ArrayList<>();

// Add completed transfers to the list
        for (Transfer transfer : group.getCompletedTransfers()) {
            sortedPanels.add(new SortablePanel(transfer.getDate(), transfer.getPanel()));
        }

// Add expenses to the list
        for (Expense expense : group.getExpenses()) {
            sortedPanels.add(new SortablePanel(expense.getDate(), expense.getPanel()));
        }

// Sort the panels based on date
        sortedPanels.sort(Comparator.comparing(SortablePanel::getDate).reversed());

// Add sorted panels to the actionsPanel
        for (SortablePanel sortablePanel : sortedPanels) {
            actionsPanel.add(sortablePanel.getPanel(), Layout.getGridBagElementConstraints());
        }

        JScrollPane transfersScrollPane = new JScrollPane(actionsPanel);


        //importActions();

        JButton addPaymentButton = new JButton("Dodaj nową płatność");

        userList = new JList<>(group.getUsers().stream().map(User::getName).toArray(String[]::new));
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = userList.getSelectedIndex();

                if (selectedIndex != -1) {
                    User selectedUser = group.getUsers().get(selectedIndex);
                    selectedUser.showUserDetails();
                }
            }
        });
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
            // TODO
            showPaymentDialog(group);
        });
    }

    private void showPaymentDialog(Group group) {
        ExpenseDialog dialog = new ExpenseDialog(group);

        if (dialog.isExpenseAdded()) {
            Expense expense = dialog.getExpense();
            group.addExpense(expense);
            // Moim zdaniem wygodniej jest, żeby akcje nowsze były na górze, tak jak np. historia przelewów w banku
            // Zgadzam się, nie umiałem :)
            actionsPanel.add(expense.getPanel(), Layout.getGridBagElementConstraints(), 0);
        }
    }

    private void importActions() {
        List<MoneyAction> actionsList = group.getActionsList();

        for (MoneyAction action : actionsList) {
            actionsPanel.add(action.getPanel(), Layout.getGridBagElementConstraints());
        }
    }

    class SortablePanel implements Comparable<SortablePanel> {
        private Date date;
        private JPanel panel;

        public SortablePanel(Date date, JPanel panel) {
            this.date = date;
            this.panel = panel;
        }

        public Date getDate() {
            return date;
        }

        public JPanel getPanel() {
            return panel;
        }

        @Override
        public int compareTo(SortablePanel other) {
            // Compare based on the date
            return this.date.compareTo(other.date);
        }
    }
}
