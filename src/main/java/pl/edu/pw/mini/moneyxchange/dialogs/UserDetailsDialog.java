package pl.edu.pw.mini.moneyxchange.dialogs;

import pl.edu.pw.mini.moneyxchange.data.Expense;
import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.Transfer;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.utils.Layout;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class UserDetailsDialog extends JDialog {
    private final User user;

    private JLabel userInfoLabel;
    private JLabel profilePictureLabel;
    private JPanel expensesPanel;
    private JPanel completedTransfersPanel;
    private JPanel pendingTransfersPanel;

    public UserDetailsDialog(User user) {
        super((Frame) null, "Szczegóły użytkownika " + user.getName(), true);
        this.user = user;

        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel userInfoPanel = createUserInfoPanel();
        JPanel expensesPanel = createExpensesPanel();
        JPanel pendingTransfersPanel = createPendingTransfersPanel();
        JPanel completedTransfersPanel = createCompletedTransfersPanel();

        tabbedPane.addTab("Informacje", userInfoPanel);
        tabbedPane.addTab("Wydatki", expensesPanel);
        tabbedPane.addTab("Oczekujące przelewy", pendingTransfersPanel);
        tabbedPane.addTab("Wykonane przelewy", completedTransfersPanel);

        add(tabbedPane, BorderLayout.CENTER);


        user.addListener(evt -> {
            if (evt.getPropertyName().equals("name")) {
                userInfoLabel.setText((String) evt.getNewValue());
            } else if (evt.getPropertyName().equals("image")) {
                if (evt.getNewValue() != null) {
                    ImageIcon imageIcon = new ImageIcon(((BufferedImage) evt.getNewValue()).getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                    profilePictureLabel.setIcon(imageIcon);
                } else {
                    profilePictureLabel.setIcon(null);
                }
            }
        });
    }

    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        userInfoLabel = new JLabel("Imię: " + user.getName());
        panel.add(userInfoLabel, BorderLayout.NORTH);

        profilePictureLabel = new JLabel((ImageIcon) null);
        if (user.getImage() != null) {
            ImageIcon imageIcon = new ImageIcon(user.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            profilePictureLabel.setIcon(imageIcon);
        }
        panel.add(profilePictureLabel, BorderLayout.CENTER);

        JButton editButton = new JButton("Edytuj");
        editButton.addActionListener(e -> showEditUserDialog());
        panel.add(editButton, BorderLayout.SOUTH);

        return panel;
    }

    private void showEditUserDialog() {
        JDialog dialog = new UserDialog(user);

        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel createExpensesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Historia wydatków"));

        expensesPanel = new JPanel(new GridBagLayout());  // Utwórz panel dla wydatków
        JScrollPane expensesScrollPane = new JScrollPane(expensesPanel);

        panel.add(expensesScrollPane, BorderLayout.CENTER);

        JButton addExpenseButton = new JButton("Dodaj wydatek");
        addExpenseButton.addActionListener(e -> {
            ExpenseDialog expenseDialog = new ExpenseDialog(Group.getInstance());
            expenseDialog.setSize(400, 300);
            expenseDialog.setVisible(true);
            expenseDialog.setLocationRelativeTo(null);

            if (expenseDialog.isExpenseAdded()) {
                Expense expense = expenseDialog.getExpense();
                Group.getInstance().addExpense(expense);
            }
        });
        panel.add(addExpenseButton, BorderLayout.SOUTH);

        displayExpenses(user.getExpenses());

        user.addListener(evt -> {
            if (!evt.getPropertyName().equals("expenses")) return;

            //noinspection unchecked
            displayExpenses((List<Expense>) evt.getNewValue());
        });

        return panel;
    }

    private JPanel createPendingTransfersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Historia przelewów"));

        pendingTransfersPanel = new JPanel(new GridBagLayout());
        JScrollPane pendingTransfersScrollPane = new JScrollPane(pendingTransfersPanel);

        panel.add(pendingTransfersScrollPane, BorderLayout.CENTER);

        displayPendingTransfers(user.getPendingTransfers());

        user.addListener(evt -> {
            if (!evt.getPropertyName().equals("pendingTransfers")) return;

            //noinspection unchecked
            displayPendingTransfers((List<Transfer>) evt.getNewValue());
        });

        return panel;
    }

    private JPanel createCompletedTransfersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Historia przelewów"));

        completedTransfersPanel = new JPanel(new GridBagLayout());  // Utwórz panel dla wydatków
        JScrollPane completedTransfersScrollPane = new JScrollPane(completedTransfersPanel);

        panel.add(completedTransfersScrollPane, BorderLayout.CENTER);
        JButton addTransferButton = new JButton("Dodaj przelew");

        addTransferButton.addActionListener(e -> {
            TransferDialog transferDialog = new TransferDialog(Group.getInstance());
            transferDialog.setVisible(true);
            transferDialog.setLocationRelativeTo(null);
        });
        panel.add(addTransferButton, BorderLayout.SOUTH);

        displayCompletedTransfers(user.getCompletedTransfers());

        user.addListener(evt -> {
            if (!evt.getPropertyName().equals("completedTransfers")) return;

            //noinspection unchecked
            displayCompletedTransfers((List<Transfer>) evt.getNewValue());
        });

        return panel;
    }

    private void displayExpenses(List<Expense> expenses) {
        expensesPanel.removeAll(); // Usunięcie wszystkich komponentów przed dodaniem nowych

        for (Expense expense : expenses) {
            JPanel expensePanel = expense.getPanel();
            expensesPanel.add(expensePanel, Layout.getGridBagElementConstraints());
        }

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(0, 0));
        expensesPanel.add(spacer, Layout.getGridBagSpacerConstraints());

        expensesPanel.revalidate();
        expensesPanel.repaint();
    }

    private void displayPendingTransfers(List<Transfer> transfers) {
        pendingTransfersPanel.removeAll();
        for (Transfer transfer : transfers) {
            JPanel transferPanel = transfer.getPendingPanel();
            pendingTransfersPanel.add(transferPanel, Layout.getGridBagElementConstraints());
        }

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(0, 0));
        pendingTransfersPanel.add(spacer, Layout.getGridBagSpacerConstraints());

        pendingTransfersPanel.revalidate();
        pendingTransfersPanel.repaint();
    }

    private void displayCompletedTransfers(List<Transfer> transfers) {
        completedTransfersPanel.removeAll();

        for (Transfer transfer : transfers) {
            JPanel transferPanel = transfer.getPanel();
            completedTransfersPanel.add(transferPanel, Layout.getGridBagElementConstraints());
        }

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(0, 0));
        completedTransfersPanel.add(spacer, Layout.getGridBagSpacerConstraints());

        completedTransfersPanel.revalidate();
        completedTransfersPanel.repaint();
    }
}
