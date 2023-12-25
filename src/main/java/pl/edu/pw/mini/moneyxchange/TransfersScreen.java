package pl.edu.pw.mini.moneyxchange;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TransfersScreen extends JPanel {
    private List<Transfer> transfers;
    private final JPanel transfersPanel;

    public TransfersScreen() {
        transfers = new ArrayList<>();
        transfers.add(new Transfer("Dinner", "2023-01-15", 50.0, List.of("User1", "User2")));
        transfers.add(new Transfer("Groceries", "2023-01-20", 30.0, List.of("User3", "User4", "User5")));
        transfers.add(new Transfer("Dinner", "2023-01-15", 50.0, List.of("User1", "User2")));
        transfers.add(new Transfer("Groceries", "2023-01-20", 30.0, List.of("User3", "User4", "User5")));
        transfers.add(new Transfer("Dinner", "2023-01-15", 50.0, List.of("User1", "User2")));
        transfers.add(new Transfer("Groceries", "2023-01-20", 30.0, List.of("User3", "User4", "User5")));

        // Create components
        transfersPanel = new JPanel();
        JScrollPane transfersScrollPane = new JScrollPane(transfersPanel);
        JButton addTransferButton = new JButton("Dodaj przelew");
        JButton filterButton = new JButton("Filtruj...");

        // Add padding to the main panel
        int padding = 10;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(padding, padding, padding, padding));

        // Add components to the layout
        add(transfersScrollPane, BorderLayout.CENTER);
        add(addTransferButton, BorderLayout.SOUTH);
        add(filterButton, BorderLayout.NORTH);

        // Set up the transfers panel
        transfersPanel.setLayout(new GridLayout(0, 1));  // Use GridLayout with one column
        displayTransfers();

        // Add action listener for the "Add Transfer" button
        addTransferButton.addActionListener(e -> showAddTransferDialog());

        // Add action listener for the "Filter" button
        filterButton.addActionListener(e -> showFilterDialog());
    }

    private void displayTransfers() {
        transfersPanel.removeAll();

        for (Transfer transfer : transfers) {
            JPanel transferPanel = createTransferPanel(transfer);
            transfersPanel.add(transferPanel);
        }

        transfersPanel.revalidate();
        transfersPanel.repaint();
    }


    private JPanel createTransferPanel(Transfer transfer) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setLayout(new GridLayout(4, 1));

        JLabel titleLabel = new JLabel("Tytuł: " + transfer.getTitle());
        JLabel dateLabel = new JLabel("Data: " + transfer.getDate());
        JLabel amountLabel = new JLabel("Kwota: $" + transfer.getAmount());
        JLabel participantsLabel = new JLabel("Uczestnicy: " + String.join(", ", transfer.getParticipants()));

        panel.add(titleLabel);
        panel.add(dateLabel);
        panel.add(amountLabel);
        panel.add(participantsLabel);

        // Set a fixed size for the panel
        panel.setPreferredSize(new Dimension(0, 100)); // Adjust the width and height as needed

        return panel;
    }

    private void showAddTransferDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Dodaj przelew", true);
        dialog.setLayout(new BorderLayout());

        JTextField titleField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField participantsField = new JTextField();

        JButton addButton = new JButton("Dodaj przelew");

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Tytuł:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Data:"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Kwota:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Uczestnicy (oddzielone przecinkiem):"));
        inputPanel.add(participantsField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listener for the "Add Transfer" button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get input values
                String title = titleField.getText();
                String date = dateField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String participantsText = participantsField.getText();
                List<String> participants = new ArrayList<>(List.of(participantsText.split(",")));

                // Create a new transfer
                Transfer newTransfer = new Transfer(title, date, amount, participants);
                transfers.add(newTransfer);

                // Update the display
                displayTransfers();

                // Close the dialog
                dialog.dispose();
            }
        });

        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void showFilterDialog() {
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Filter Transfers", true);
        filterDialog.setLayout(new BorderLayout());

        // Create radio buttons for filter options
        JRadioButton titleRadioButton = new JRadioButton("Filter by Title");
        JRadioButton dateRadioButton = new JRadioButton("Filter by Date");
        JRadioButton amountRadioButton = new JRadioButton("Filter by Amount");

        // Group the radio buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(titleRadioButton);
        buttonGroup.add(dateRadioButton);
        buttonGroup.add(amountRadioButton);

        // Create the "Filter" button
        JButton applyFilterButton = new JButton("Apply Filter");

        // Add components to the filter dialog
        JPanel optionsPanel = new JPanel(new GridLayout(3, 1));
        optionsPanel.add(titleRadioButton);
        optionsPanel.add(dateRadioButton);
        optionsPanel.add(amountRadioButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(applyFilterButton);

        filterDialog.add(optionsPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listener for the "Apply Filter" button
        applyFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Determine the selected filter option
                if (titleRadioButton.isSelected()) {
                    // Filter by title
                    applyTitleFilter();
                } else if (dateRadioButton.isSelected()) {
                    // Filter by date
                    applyDateFilter();
                } else if (amountRadioButton.isSelected()) {
                    // Filter by amount
                    applyAmountFilter();
                }

                // Close the dialog
                filterDialog.dispose();
            }
        });

        // Set dialog properties
        filterDialog.setSize(300, 200);
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }

    private void applyTitleFilter() {
        String keyword = JOptionPane.showInputDialog(this, "Enter Title Keyword:");
        if (keyword != null) {
            List<Transfer> filteredTransfers = new ArrayList<>();
            for (Transfer transfer : transfers) {
                if (transfer.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredTransfers.add(transfer);
                }
            }
            transfers = filteredTransfers;
            displayTransfers();
        }
    }

    private void applyDateFilter() {
        String keyword = JOptionPane.showInputDialog(this, "Enter Date Keyword:");
        if (keyword != null) {
            List<Transfer> filteredTransfers = new ArrayList<>();
            for (Transfer transfer : transfers) {
                if (transfer.getDate().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredTransfers.add(transfer);
                }
            }
            transfers = filteredTransfers;
            displayTransfers();
        }
    }

    private void applyAmountFilter() {
        String keyword = JOptionPane.showInputDialog(this, "Enter Amount Keyword:");
        if (keyword != null) {
            try {
                double amount = Double.parseDouble(keyword);
                List<Transfer> filteredTransfers = new ArrayList<>();
                for (Transfer transfer : transfers) {
                    if (transfer.getAmount() == amount) {
                        filteredTransfers.add(transfer);
                    }
                }
                transfers = filteredTransfers;
                displayTransfers();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.");
            }
        }
    }
}