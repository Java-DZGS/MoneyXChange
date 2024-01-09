package pl.edu.pw.mini.moneyxchange;

import pl.edu.pw.mini.moneyxchange.data.Expense;
import pl.edu.pw.mini.moneyxchange.data.Group;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class HistoryScreen extends JPanel {
    private final JPanel historyPanel;
    private final List<Expense> expenses;

    public HistoryScreen() {
        historyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        historyPanel.add(new JPanel(), gbc);
        historyPanel.setLayout(new GridLayout(0, 1));

        JScrollPane historyScrollPane = new JScrollPane(historyPanel);

        int padding = 10;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(padding, padding, padding, padding));

        add(historyScrollPane, BorderLayout.CENTER);

        expenses = Group.getInstance().getExpenses();
        displayExpenses();
    }

    private void displayExpenses() {
        historyPanel.removeAll();

        for (Expense expense : expenses) {
            historyPanel.add(expense.getPanel());
        }

        historyPanel.revalidate();
        historyPanel.repaint();
    }
}
