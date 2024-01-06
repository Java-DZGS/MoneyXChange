package pl.edu.pw.mini.moneyxchange;

import pl.edu.pw.mini.moneyxchange.data.Group;

import javax.swing.*;
import java.awt.*;

public class HistoryScreen extends JPanel {
    private final JPanel historyPanel;

    public HistoryScreen() {
        Group group = Group.getInstance();

        historyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        historyPanel.add(new JPanel(), gbc);

        JScrollPane transfersScrollPane = new JScrollPane(historyPanel);
    }
}
