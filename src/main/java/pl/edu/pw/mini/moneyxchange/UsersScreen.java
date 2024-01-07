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
        panel.add(detailsButton,BorderLayout.EAST);

        detailsButton.addActionListener(e -> displayUser(user));

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

    private void displayUser(User user){
        usersPanel.removeAll();

        JPanel userPanel = createUserDetailsPanel(user);
        usersPanel.add(userPanel);
        usersPanel.revalidate();

        usersPanel.repaint();
    }

    private JPanel createUserDetailsPanel(User user){
        JPanel panel = new JPanel(new GridLayout(0, 1));

        // Informacje o użytkowniku
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setBorder(BorderFactory.createTitledBorder("Informacje o użytkowniku"));
        userInfoPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel userInfoLabel = new JLabel("Imię: ");
        JTextField nameField = new JTextField(user.getName());
        nameField.setEditable(false);
        topPanel.add(userInfoLabel);
        topPanel.add(nameField);

        userInfoPanel.add(topPanel, BorderLayout.NORTH);

        JButton editButton = new JButton("Edytuj");
        editButton.addActionListener(e -> showEditUserDialog(user));
        userInfoPanel.add(editButton, BorderLayout.SOUTH);

        // Wydatki
        JPanel expensesPanel = new JPanel(new GridBagLayout());
        expensesPanel.setLayout(new GridLayout(0, 1));
        JScrollPane expensesScrollPane = new JScrollPane(expensesPanel);
        expensesPanel.setBorder(BorderFactory.createTitledBorder("Historia wydatków"));

        JButton addExpenseButton = new JButton("Dodaj wydatek");
        addExpenseButton.addActionListener(e -> {
            ExpenseDialog expenseDialog = new ExpenseDialog(Group.getInstance());
            expenseDialog.setLocationRelativeTo(null);
            expenseDialog.setVisible(true);
            displayExpenses(user.getExpenses(), expensesPanel);
        });
        displayExpenses(user.getExpenses(), expensesPanel);
        expensesPanel.add(addExpenseButton, BorderLayout.SOUTH);
        add(expensesScrollPane, BorderLayout.CENTER);

// Dokonane przelewy
        JPanel completedTransfersPanel = new JPanel(new GridBagLayout());
        completedTransfersPanel.setLayout(new GridLayout(0, 1));
        JScrollPane completedTransfersScrollPane = new JScrollPane(completedTransfersPanel);
        completedTransfersPanel.setBorder(BorderFactory.createTitledBorder("Dokonane przelewy"));

        JButton addTransferButton = new JButton("Dodaj przelew");
        addTransferButton.addActionListener(e -> {
            TransferDialog transferDialog = new TransferDialog(Group.getInstance());
            transferDialog.setLocationRelativeTo(null);
            transferDialog.setVisible(true);
            displayCompletedTransfers(user.getCompletedTransfers(), completedTransfersPanel);
        });
        displayCompletedTransfers(user.getCompletedTransfers(), completedTransfersPanel);
        completedTransfersPanel.add(addTransferButton, BorderLayout.SOUTH);
        add(completedTransfersScrollPane, BorderLayout.CENTER);



// Czekające przelewy
        JPanel pendingTransfersPanel = new JPanel(new GridBagLayout());
        pendingTransfersPanel.setLayout(new GridLayout(0, 1));
        JScrollPane pendingTransfersScrollPane = new JScrollPane(pendingTransfersPanel);
        pendingTransfersPanel.setBorder(BorderFactory.createTitledBorder("Oczekujące przelewy"));
        JLabel pendingTransfersLabel = new JLabel("Przelewy do wykonania");
        displayPendingTransfers(user.getPendingTransfers(), pendingTransfersPanel);
        add(completedTransfersScrollPane, BorderLayout.CENTER);


// Dodajemy sekcje do głównego panelu
        panel.add(userInfoPanel, BorderLayout.NORTH);
        panel.add(expensesPanel, BorderLayout.CENTER);
        panel.add(completedTransfersPanel, BorderLayout.CENTER);
        panel.add(pendingTransfersPanel, BorderLayout.CENTER);

        return panel;
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
            displayUser(user);

            // Close the dialog
            dialog.dispose();
        });
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    private void displayExpenses(List<Expense> expenses, JPanel expensesPanel){
        for (Expense expense : expenses) {
            JPanel expensePanel = expense.getPanel();
            expensesPanel.add(expensePanel);
        }
    }
    private void displayCompletedTransfers(List<Transfer> transfers, JPanel transfersPanel){
      //  transfersPanel.removeAll();

        for (Transfer transfer:transfers) {
            JPanel transferPanel = transfer.getPanel();
            transfersPanel.add(transferPanel);
        }
      //  transfersPanel.revalidate();
      //  transfersPanel.repaint();
    }
    private void displayPendingTransfers(List<Transfer> transfers, JPanel transfersPanel){
        for (Transfer transfer:transfers) {
            JPanel transferPanel = transfer.getPanel();
            JButton doneButton = new JButton("Oznacz jako zrobiony");
            transferPanel.add(doneButton);
            transfersPanel.add(transferPanel);
            doneButton.addActionListener(e->{
                transfer.getFromUser().addCompletedTransfer(transfer);
                transfer.getFromUser().getPendingTransfers().remove(transfer);
            });
        }
    }

}