package pl.edu.pw.mini.moneyxchange.screens;

import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.Transfer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import static pl.edu.pw.mini.moneyxchange.cashflow.MinCashFlow.minTransfers;

public class OptimalTransfersScreen extends JPanel {
    private List<Transfer> transfers;
    private final JPanel transfersPanel;
    public OptimalTransfersScreen() {
        transfers = Group.getInstance().getPendingTransfers();
        transfers = minTransfers(transfers);

        // Create components
        transfersPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        transfersPanel.add(new JPanel(), gbc);

        JScrollPane transfersScrollPane = new JScrollPane(transfersPanel);

        // Add padding to the main panel
        int padding = 10;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(padding, padding, padding, padding));

        // Add components to the layout
        add(transfersScrollPane, BorderLayout.CENTER);

        // Set up the transfers panel
        transfersPanel.setLayout(new GridLayout(0, 1));  // Use GridLayout with one column
        displayTransfers();

    }
    private void displayTransfers() {
        transfersPanel.removeAll();

        for (Transfer transfer : transfers) {
            JPanel transferPanel = transfer.getOptimalPanel();
            transfersPanel.add(transferPanel);
        }

        transfersPanel.revalidate();
        transfersPanel.repaint();
    }

}
