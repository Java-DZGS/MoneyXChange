package pl.edu.pw.mini.moneyxchange.screens;

import pl.edu.pw.mini.moneyxchange.data.Expense;
import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.utils.Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class HistoryScreen extends JPanel {
    private final JPanel historyPanel;
    private List<Expense> expenses;

    public HistoryScreen() {
        historyPanel = new JPanel(new GridBagLayout());

        JScrollPane historyScrollPane = new JScrollPane(historyPanel);

        int padding = 10;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(padding, padding, padding, padding));

        add(historyScrollPane, BorderLayout.CENTER);

        expenses = Group.getInstance().getExpenses();
        displayExpenses();

        Group.getInstance().addListener(evt -> {
            if(!evt.getPropertyName().equals("expenses")) return;

            //noinspection unchecked
            expenses = (List<Expense>) evt.getNewValue();
            displayExpenses();
        });
    }

    private void displayExpenses() {
        historyPanel.removeAll();

        for (Expense expense : expenses) {
            historyPanel.add(expense.getPanel(), Layout.getGridBagElementConstraints());
        }

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(0,0));
        historyPanel.add(spacer, Layout.getGridBagSpacerConstraints());

        historyPanel.revalidate();
        historyPanel.repaint();
    }
}
