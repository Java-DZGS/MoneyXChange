package pl.edu.pw.mini.moneyxchange.screens;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.Transfer;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.utils.Format;
import pl.edu.pw.mini.moneyxchange.utils.Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransfersScreen extends JPanel {
    private List<Transfer> transfers;
    private final JPanel transfersPanel;

    public TransfersScreen() {
        transfers = Group.getInstance().getCompletedTransfers();

        // Create components
        transfersPanel = new JPanel(new GridBagLayout());

        JScrollPane transfersScrollPane = new JScrollPane(transfersPanel);
        JButton filterButton = new JButton("Filtruj...");

        // Add padding to the main panel
        int padding = 10;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(padding, padding, padding, padding));

        // Add components to the layout
        add(transfersScrollPane, BorderLayout.CENTER);
        add(filterButton, BorderLayout.NORTH);

        // Set up the transfers panel
        displayTransfers();

        // Add action listener for the "Filter" button
        filterButton.addActionListener(e -> showFilterDialog());

        Group.getInstance().addListener(evt -> {
            if (!evt.getPropertyName().equals("completedTransfers")) return;

            //noinspection unchecked
            transfers = (List<Transfer>) evt.getNewValue();
            displayTransfers();
        });
    }

    private void displayTransfers() {
        transfersPanel.removeAll();

        for (Transfer transfer : transfers) {
            JPanel transferPanel = transfer.getPanel();
            transfersPanel.add(transferPanel, Layout.getGridBagElementConstraints());
        }

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(0, 0));
        transfersPanel.add(spacer, Layout.getGridBagSpacerConstraints());

        transfersPanel.revalidate();
        transfersPanel.repaint();
    }

    private void showAddTransferDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Dodaj przelew", true);
        dialog.setLayout(new BorderLayout());

        JTextField titleField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField fromField = new JTextField();
        JTextField toField = new JTextField();

        JButton addButton = new JButton("Dodaj przelew");

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Tytuł:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Data:"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Kwota:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Od:"));
        inputPanel.add(fromField);
        inputPanel.add(new JLabel("Do:"));
        inputPanel.add(toField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            String title = titleField.getText();
            // todo: temporary, change to datepicker
            Date date = new Date();
            Money amount = Money.of(Double.parseDouble(amountField.getText()), Format.CURRENCY);

            User fromUser = Group.getInstance().findUserByName(fromField.getText());
            User toUser = Group.getInstance().findUserByName(toField.getText());

            Transfer newTransfer = new Transfer(date, amount, fromUser, toUser);
            transfers.add(newTransfer);

            displayTransfers();

            dialog.dispose();
        });

        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void showFilterDialog() {
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Filter Transfers", true);
        filterDialog.setLayout(new BorderLayout());

        // Create radio buttons for filter options
        JRadioButton dateRadioButton = new JRadioButton("Filter by Date");
        JRadioButton amountRadioButton = new JRadioButton("Filter by Amount");

        // Group the radio buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(dateRadioButton);
        buttonGroup.add(amountRadioButton);

        // Create the "Filter" button
        JButton applyFilterButton = new JButton("Apply Filter");

        // Add components to the filter dialog
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1));
        optionsPanel.add(dateRadioButton);
        optionsPanel.add(amountRadioButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(applyFilterButton);

        filterDialog.add(optionsPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listener for the "Apply Filter" button
        applyFilterButton.addActionListener(e -> {
            // Determine the selected filter option
            if (dateRadioButton.isSelected()) {
                // Filter by date
                applyDateFilter();
            } else if (amountRadioButton.isSelected()) {
                // Filter by amount
                applyAmountFilter();
            }

            // Close the dialog
            filterDialog.dispose();
        });

        // Set dialog properties
        filterDialog.setSize(300, 200);
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }
    private void applyDateFilter() {
        String keyword = JOptionPane.showInputDialog(this, "Enter Date Keyword:");
        if (keyword != null) {
            List<Transfer> filteredTransfers = new ArrayList<>();
            // todo: filter transfers by date; commented out because date isn't a string anymore
            /*
            for (Transfer transfer : transfers) {
                if (transfer.getDate().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredTransfers.add(transfer);
                }
            }
             */
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
                    if (transfer.getAmount().getNumber().doubleValue() == amount) {
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