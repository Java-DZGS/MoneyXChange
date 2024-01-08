package pl.edu.pw.mini.moneyxchange;

import pl.edu.pw.mini.moneyxchange.data.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;


public class UsersScreen extends JPanel{
    private List<User> users;
    private final JPanel usersPanel;
    private JPanel completedPanel;
    public UsersScreen() {
        users = Group.getInstance().getUsers();

        usersPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        usersPanel.add(new JPanel(), gbc);

        JScrollPane usersScrollPlane = new JScrollPane(usersPanel);
        JButton addUserButton = new JButton("Dodaj użytkownika");

        int padding = 10;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(padding, padding, padding, padding));

        add(usersScrollPlane, BorderLayout.CENTER);
        add(addUserButton, BorderLayout.SOUTH);

        usersPanel.setLayout(new GridLayout(0, 1));  // Use GridLayout with one column
        displayUsers();

        addUserButton.addActionListener(e -> showAddUserDialog());
    }
    private void displayUsers(){
        usersPanel.removeAll();

        for (User user: users) {
            JPanel userPanel = createUserPanel(user);
            usersPanel.add(userPanel);
        }
        usersPanel.revalidate();
        usersPanel.repaint();
    }
    private JPanel createUserPanel(User user) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        int padding = 10;
        panel.setLayout(new GridLayout(1, 1));

        JLabel nameLabel = new JLabel(user.getName());
        JButton detailsButton = new JButton("Szczegóły");

        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 10));

        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(detailsButton, BorderLayout.EAST);

        detailsButton.addActionListener(e -> showUserDetails(user));

        return panel;
    }
    private void showUserDetails(User user) {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel userInfoPanel = createUserInfoPanel(user);
        JPanel expensesPanel = createExpensesPanel(user);
        JPanel completedTransfersPanel = createCompletedTransfersPanel(user);
        JPanel pendingTransfersPanel = createPendingTransfersPanel(user, completedTransfersPanel);

        tabbedPane.addTab("Informacje", userInfoPanel);
        tabbedPane.addTab("Wydatki", expensesPanel);
        tabbedPane.addTab("Wykonane przelewy", completedTransfersPanel);
        tabbedPane.addTab("Oczekujące przelewy", pendingTransfersPanel);

        JFrame userDetailsFrame = new JFrame("Szczegóły użytkownika - " + user.getName());
        userDetailsFrame.setLayout(new BorderLayout());
        userDetailsFrame.add(tabbedPane, BorderLayout.CENTER);
        userDetailsFrame.setSize(500, 400);
        userDetailsFrame.setLocationRelativeTo(null);
        userDetailsFrame.setVisible(true);
    }

    private JPanel createUserInfoPanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel userInfoLabel = new JLabel("Imię: " + user.getName());
        panel.add(userInfoLabel, BorderLayout.NORTH);

        JButton editButton = new JButton("Edytuj");
        editButton.addActionListener(e -> showEditUserDialog(user));
        panel.add(editButton, BorderLayout.SOUTH);

        return panel;
    }
    private JPanel createExpensesPanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Historia wydatków"));

        JPanel expensesPanel = new JPanel(new GridBagLayout());  // Utwórz panel dla wydatków
        expensesPanel.setLayout(new GridLayout(0, 1));  // Ustaw GridLayout z jedną kolumną
        JScrollPane expensesScrollPane = new JScrollPane(expensesPanel);

        panel.add(expensesScrollPane, BorderLayout.CENTER);

        JButton addExpenseButton = new JButton("Dodaj wydatek");
        addExpenseButton.addActionListener(e -> {
            ExpenseDialog expenseDialog = new ExpenseDialog(Group.getInstance());
            expenseDialog.setVisible(true);
            expenseDialog.setLocationRelativeTo(null);
            // Po dodaniu nowego wydatku odśwież historię wydatków
            displayExpenses(user.getExpenses(), expensesPanel);
        });
        panel.add(addExpenseButton, BorderLayout.SOUTH);

        displayExpenses(user.getExpenses(), expensesPanel);

        return panel;
    }

    private JPanel createCompletedTransfersPanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Historia przelewów"));

        JPanel completedTransfersPanel = new JPanel(new GridBagLayout());  // Utwórz panel dla wydatków
        completedTransfersPanel.setLayout(new GridLayout(0, 1));  // Ustaw GridLayout z jedną kolumną
        completedPanel = completedTransfersPanel;
        JScrollPane completedTransfersScrollPane = new JScrollPane(completedTransfersPanel);

        panel.add(completedTransfersScrollPane, BorderLayout.CENTER);
        JButton addTransferButton = new JButton("Dodaj przelew");

        addTransferButton.addActionListener(e -> {
            TransferDialog transferDialog = new TransferDialog(Group.getInstance());
            transferDialog.setVisible(true);
            transferDialog.setLocationRelativeTo(null);
            displayCompletedTransfers(user.getCompletedTransfers(),completedTransfersPanel);
        });
        panel.add(addTransferButton, BorderLayout.SOUTH);

        displayCompletedTransfers(user.getCompletedTransfers(), completedTransfersPanel);
        return panel;
    }
    private JPanel createPendingTransfersPanel(User user, JPanel completedTransfersPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Historia przelewów"));

        JPanel pendingTransfersPanel = new JPanel(new GridBagLayout());  // Utwórz panel dla wydatków
        pendingTransfersPanel.setLayout(new GridLayout(0, 1));  // Ustaw GridLayout z jedną kolumną
        JScrollPane pendingTransfersScrollPane = new JScrollPane(pendingTransfersPanel);

        panel.add(pendingTransfersScrollPane, BorderLayout.CENTER);

        displayPendingTransfers(user.getPendingTransfers(), pendingTransfersPanel, user.getCompletedTransfers(),completedTransfersPanel);
        return panel;
    }
    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Dodaj użytkownika", true);
        dialog.setLayout(new BorderLayout());

        JTextField nameField = new JTextField();
        JTextField idField = new JTextField();

        JButton addButton = new JButton("Dodaj użytkownika");

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Imię:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Id:"));
        inputPanel.add(idField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listener for the "Add Transfer" button
        addButton.addActionListener(e -> {
            // Get input values
            String name = nameField.getText();
            int id = 0;
            try {
                // Konwersja tekstu z pola tekstowego na int
                id = Integer.parseInt(idField.getText());
            } catch (NumberFormatException ex) {
                // Obsługa błędu, gdy wprowadzony tekst nie jest liczbą całkowitą
                JOptionPane.showMessageDialog(this, "To nie jest liczba całkowita. Spróbuj ponownie.", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User newUser = new User(name, id);
            Group.getInstance().addUser(newUser);

            // Update the display
            displayUsers();

            // Close the dialog
            dialog.dispose();
        });

        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditUserDialog(User user) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edytuj użytkownika", true);
        dialog.setLayout(new BorderLayout());

        JTextField nameField = new JTextField(user.getName());

        JButton saveButton = new JButton("Zapisz zmiany");

        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        inputPanel.add(new JLabel("Imię:"));
        inputPanel.add(nameField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            // Get input values
            String newName = nameField.getText();
            user.setName(newName);

            // Update the display
            // displayUser(user);

            // Close the dialog
            dialog.dispose();
        });
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    private void displayExpenses(List<Expense> expenses, JPanel expensesPanel){
        expensesPanel.removeAll(); // Usunięcie wszystkich komponentów przed dodaniem nowych

        for (Expense expense : expenses) {
            JPanel expensePanel = expense.getPanel();
            expensesPanel.add(expensePanel);
        }

        expensesPanel.revalidate();
        expensesPanel.repaint();
    }
    private void displayCompletedTransfers(List<Transfer> transfers, JPanel transfersPanel){
        transfersPanel.removeAll();

        for (Transfer transfer:transfers) {
            JPanel transferPanel = transfer.getPanel();
            transfersPanel.add(transferPanel);
        }

        transfersPanel.revalidate();
        transfersPanel.repaint();
    }
    private void displayPendingTransfers(List<Transfer> transfers, JPanel transfersPanel, List<Transfer> completedTransfers,JPanel completedTransfersPanel){
        transfersPanel.removeAll();
        for (Transfer transfer:transfers) {
            JPanel transferPanel = transfer.getPanel();
            JButton doneButton = new JButton("Oznacz jako zrobiony");
            transferPanel.add(doneButton);
            transfersPanel.add(transferPanel);
            doneButton.addActionListener(e->{
                transfer.getFromUser().addCompletedTransfer(transfer);
                transfer.getFromUser().getPendingTransfers().remove(transfer);

                //JPanel panel = transfer.getPanel();
                //completedTransfersPanel.add(panel);
                //completedTransfersPanel.revalidate();
                //completedTransfersPanel.repaint();

                displayPendingTransfers(transfers,transfersPanel, completedTransfers,completedTransfersPanel);
                displayCompletedTransfers(completedTransfers, completedPanel);
            });
        }
        transfersPanel.revalidate();
        transfersPanel.repaint();
    }

}