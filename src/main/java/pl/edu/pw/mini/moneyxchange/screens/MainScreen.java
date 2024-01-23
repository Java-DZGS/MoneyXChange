package pl.edu.pw.mini.moneyxchange.screens;

import pl.edu.pw.mini.moneyxchange.data.*;
import pl.edu.pw.mini.moneyxchange.dialogs.ExpenseDialog;
import pl.edu.pw.mini.moneyxchange.dialogs.FilterDialog;
import pl.edu.pw.mini.moneyxchange.utils.Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public class MainScreen extends JPanel {
    private final JPanel actionsPanel;
    private FilterDialog.FilterCriteria filterCriteria;
    private final JList<String> userList;

    public MainScreen() {
        JLabel groupNameLabel = new JLabel(Group.getInstance().getName());

        groupNameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton changeNameButton = new JButton("Zmień nazwę grupy");
        JButton serializeButton = new JButton("Serializuj grupę do pliku");
        JButton deserializeButton = new JButton("Deserializuj grupę z pliku");

        actionsPanel = new JPanel(new GridBagLayout());
        showActions();

        JScrollPane actionScrollPane = new JScrollPane(actionsPanel);

        JButton addExpenseButton = new JButton("Dodaj nową płatność");

        userList = new JList<>(Group.getInstance().getUsers().stream().map(User::getName).toArray(String[]::new));
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = userList.getSelectedIndex();

                if (selectedIndex != -1) {
                    User selectedUser = Group.getInstance().getUsers().get(selectedIndex);
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

        JButton filterButton = new JButton("Filtruj");
        filterButton.addActionListener(e -> showFilterDialog());
        topPanel.add(filterButton);

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(new JLabel("Historia akcji"), BorderLayout.NORTH);
        historyPanel.add(actionScrollPane, BorderLayout.CENTER);
        historyPanel.add(addExpenseButton, BorderLayout.SOUTH);

        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.add(new JLabel("Lista członków grupy"), BorderLayout.NORTH);
        usersPanel.add(userListScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, historyPanel, usersPanel);
        splitPane.setResizeWeight(0.75);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        changeNameButton.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog("Wprowadź nową nazwę grupy:", Group.getInstance().getName());
            if (newName == null)
                return;

            Group.getInstance().setName(newName);
            groupNameLabel.setText(Group.getInstance().getName());
        });

        serializeButton.addActionListener(e -> {
            try {
                Group.getInstance().serialize();
                JOptionPane.showMessageDialog(null, "Grupa zserializowana do pliku.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "W trakcie serializacji wystąpił błąd!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        deserializeButton.addActionListener(e -> {
            try {
                Group.deserialize();
                showActions();
                groupNameLabel.setText(Group.getInstance().getName());
                JOptionPane.showMessageDialog(null, "Grupa zdeserializowana z pliku.");
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "W trakcie deserializacji wystąpił błąd!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        addExpenseButton.addActionListener(e -> showExpenseDialog());

        Group.getInstance().addListener(evt -> {
            if (evt.getPropertyName().equals("action")) {
                showActions();
            } else if (evt.getPropertyName().equals("users")) {
                userList.setListData(Group.getInstance().getUsers().stream().map(User::getName).toArray(String[]::new));
            }
        });
    }

    private void showFilterDialog() {
        FilterDialog filterDialog = new FilterDialog((Frame) SwingUtilities.getWindowAncestor(this));
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);

        if (filterDialog.isFilterApplied())
            filterCriteria = filterDialog.getFilterCriteria();

        showActions();
    }

    private void showExpenseDialog() {
        ExpenseDialog dialog = new ExpenseDialog();
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showActions() {
        actionsPanel.removeAll();

        Group.getInstance().getActionsList()
                .stream()
                .filter(action -> filterCriteria == null || filterCriteria.applyFilter(action))
                .forEach(action -> actionsPanel.add(action.getPanel(), Layout.getGridBagElementConstraints()));

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(0, 0));
        actionsPanel.add(spacer, Layout.getGridBagSpacerConstraints());

        actionsPanel.revalidate();
        actionsPanel.repaint();
    }
}
