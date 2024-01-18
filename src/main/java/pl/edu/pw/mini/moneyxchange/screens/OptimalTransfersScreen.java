package pl.edu.pw.mini.moneyxchange.screens;

import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.Transfer;
import pl.edu.pw.mini.moneyxchange.utils.Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class OptimalTransfersScreen extends JPanel {
    private List<Transfer> transfers;
    private final JPanel transfersPanel;
    public OptimalTransfersScreen() {
        transfers = Group.getInstance().getPendingTransfers();

        // Create components
        transfersPanel = new JPanel(new GridBagLayout());
        JScrollPane transfersScrollPane = new JScrollPane(transfersPanel);

        // Add padding to the main panel
        int padding = 10;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(padding, padding, padding, padding));

        // Add components to the layout
        add(transfersScrollPane, BorderLayout.CENTER);

        // Set up the transfers panel
        displayTransfers();

        Group.getInstance().addListener(evt -> {
            if(!evt.getPropertyName().equals("pendingTransfers")) return;

            //noinspection unchecked
            transfers = Group.getInstance().getPendingTransfers();
            displayTransfers();
        });

    }

    private void displayTransfers() {
        transfersPanel.removeAll();

        for (Transfer transfer : transfers) {
            JPanel transferPanel = transfer.getOptimalPanel();
            transfersPanel.add(transferPanel, Layout.getGridBagElementConstraints());
        }

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(0, 0));
        transfersPanel.add(spacer, Layout.getGridBagSpacerConstraints());

        transfersPanel.revalidate();
        transfersPanel.repaint();
    }
}
