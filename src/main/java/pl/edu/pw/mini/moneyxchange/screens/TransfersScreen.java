package pl.edu.pw.mini.moneyxchange.screens;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.Expense;
import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.Transfer;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.dialogs.FilterDialog;
import pl.edu.pw.mini.moneyxchange.utils.Format;
import pl.edu.pw.mini.moneyxchange.utils.Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
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
        FilterDialog filterDialog = new FilterDialog((Frame) SwingUtilities.getWindowAncestor(this));
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);

        // Process the selected filter criteria from the dialog
        if (filterDialog.isFilterApplied()) {
            FilterDialog.FilterCriteria filterCriteria = filterDialog.getFilterCriteria();
            // Implement filtering logic based on the selected criteria
            filterExpenses(filterCriteria);
        }
    }

    private void filterExpenses(FilterDialog.FilterCriteria filterCriteria) {
        /*

        List<Expense> allExpenses = Group.getInstance().getExpenses();
        List<Expense> filteredExpenses = new ArrayList<>();

        for (Expense expense : allExpenses) {
            boolean dateMatch = filterCriteria.getDates() == null || filterCriteria.getDates().length == 0 || Arrays.asList(filterCriteria.getDates()).contains(expense.getDate());
            boolean participantMatch = filterCriteria.getParticipants() == null || filterCriteria.getParticipants().length == 0 || Arrays.asList(filterCriteria.getParticipants()).contains(expense.getParticipants());
            boolean payerMatch = filterCriteria.getPayer() == null || filterCriteria.getPayer().isEmpty() || filterCriteria.getPayer().equals(expense.getPayer().getName());
            if (dateMatch && participantMatch && payerMatch) {
                filteredExpenses.add(expense);
            }
        }

        if (filteredExpenses.isEmpty()) {
            // Show a warning dialog if the filtered list is empty
            JOptionPane.showMessageDialog(this, "Żadne wydatki nie pasują do nałożonych filtrów.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            expenses = filteredExpenses;

            updateChart();
        }
         */
    }
}