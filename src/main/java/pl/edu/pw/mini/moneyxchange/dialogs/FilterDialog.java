package pl.edu.pw.mini.moneyxchange.dialogs;

import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class FilterDialog extends JDialog {

    private boolean filterApplied;
    private FilterCriteria filterCriteria;

    public FilterDialog(Frame owner) {
        super(owner, "Opcje filtrowania", true);

        filterApplied = false;
        filterCriteria = new FilterCriteria();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        // First Row
        JLabel dateLabel = new JLabel("Data:");
        JLabel fromLabel = new JLabel("od");
        UtilDateModel fromDateModel = new UtilDateModel();
        JDatePanelImpl fromDatePanel = new JDatePanelImpl(fromDateModel, Format.DATE_PICKER_PROPERTIES);
        JDatePickerImpl fromDatePicker = new JDatePickerImpl(fromDatePanel, Format.DATE_LABEL_FORMATTER);
        fromDatePicker.setPreferredSize(new Dimension(110,25));
        JLabel toLabel = new JLabel("do");
        UtilDateModel toDateModel = new UtilDateModel();
        JDatePanelImpl toDatePanel = new JDatePanelImpl(toDateModel, Format.DATE_PICKER_PROPERTIES);
        JDatePickerImpl toDatePicker = new JDatePickerImpl(toDatePanel, Format.DATE_LABEL_FORMATTER);
        toDatePicker.setPreferredSize(new Dimension(110,25));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(dateLabel, gbc);
        gbc.gridx = 1;
        add(fromLabel, gbc);
        gbc.gridx = 2;
        add(fromDatePicker, gbc);
        gbc.gridx = 3;
        add(toLabel, gbc);
        gbc.gridx = 4;
        add(toDatePicker, gbc);

        // Second Row
        JLabel amountLabel = new JLabel("Kwota:");
        JLabel fromAmountLabel = new JLabel("od");
        JTextField fromAmountField = new JTextField(6);
        JLabel toAmountLabel = new JLabel("do");
        JTextField toAmountField = new JTextField(6);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(amountLabel, gbc);
        gbc.gridx = 1;
        add(fromAmountLabel, gbc);
        gbc.gridx = 2;
        add(fromAmountField, gbc);
        gbc.gridx = 3;
        add(toAmountLabel, gbc);
        gbc.gridx = 4;
        add(toAmountField, gbc);

        // Third Row
        JLabel titleLabel = new JLabel("Tytuł:");
        JTextField titleField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(titleLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 4; // Span across four columns
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(titleField, gbc);

        JButton applyButton = new JButton("Zatwierdź");
        applyButton.addActionListener(e -> {
            filterCriteria.setFromDate("fromDateField.getText()");
            filterCriteria.setToDate("");
            filterCriteria.setFromAmount(fromAmountField.getText());
            filterCriteria.setToAmount(toAmountField.getText());
            filterCriteria.setTitle(titleField.getText());
            filterApplied = true;
            setVisible(false);
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 5;
        add(applyButton, gbc);

        pack();

    }


    public boolean isFilterApplied() {
        return filterApplied;
    }

    public FilterCriteria getFilterCriteria() {
        return filterCriteria;
    }

    public static class FilterCriteria {

        private String[] dates;
        private String[] participants;
        private String payer;

        public String[] getDates() {
            return dates;
        }

        public void setDates(String[] dates) {
            this.dates = dates;
        }

        public String[] getParticipants() {
            return participants;
        }

        public void setParticipants(String[] participants) {
            this.participants = participants;
        }

        public String getPayer() {
            return payer;
        }

        public void setPayer(String payer) {
            this.payer = payer;
        }

        public void setFromDate(String text) {

        }

        public void setToDate(String text) {

        }

        public void setFromAmount(String text) {

        }

        public void setToAmount(String text) {

        }

        public void setTitle(String text) {
        }
    }
}

/*
Dialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Filter Transfers", true);
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
 */



