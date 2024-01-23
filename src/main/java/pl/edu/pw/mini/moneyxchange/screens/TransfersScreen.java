package pl.edu.pw.mini.moneyxchange.screens;

import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.Transfer;
import pl.edu.pw.mini.moneyxchange.dialogs.FilterDialog;
import pl.edu.pw.mini.moneyxchange.utils.Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TransfersScreen extends JPanel {
    private List<Transfer> transfers;
    private final JPanel transfersPanel;

    public TransfersScreen() {
        transfers = Group.getInstance().getCompletedTransfers();

        // Create components
        transfersPanel = new JPanel(new GridBagLayout());

        JScrollPane transfersScrollPane = new JScrollPane(transfersPanel);
        transfersScrollPane.getVerticalScrollBar().setUnitIncrement(16);
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


    private void showFilterDialog() {
        FilterDialog filterDialog = new FilterDialog((Frame) SwingUtilities.getWindowAncestor(this), false);
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);

        if (filterDialog.isFilterApplied()) {
            FilterDialog.FilterCriteria filterCriteria = filterDialog.getFilterCriteria();
            filterExpenses(filterCriteria);
        }
    }

    private void filterExpenses(FilterDialog.FilterCriteria filterCriteria) {
        List<Transfer> allTransfers = Group.getInstance().getCompletedTransfers();
        List<Transfer> filteredTransfers = new ArrayList<>();

        for (Transfer transfer : allTransfers) {
            if (filterCriteria.applyFilter(transfer)) {
                filteredTransfers.add(transfer);
            }
        }

        if (filteredTransfers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Żadne wydatki nie pasują do nałożonych filtrów.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            transfers = filteredTransfers;

            displayTransfers();
        }

    }
}