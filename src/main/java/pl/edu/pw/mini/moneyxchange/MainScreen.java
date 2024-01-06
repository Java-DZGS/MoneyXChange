package pl.edu.pw.mini.moneyxchange;

import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.MoneyAction;
import pl.edu.pw.mini.moneyxchange.data.Transfer;
import pl.edu.pw.mini.moneyxchange.data.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class MainScreen extends JPanel {
    private Group group;

    private final JPanel actionsPanel;
    private final JList<String> userList;

    public MainScreen() {
        group = Group.getInstance();

        JLabel groupNameLabel = new JLabel(group.getName());
        groupNameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton changeNameButton = new JButton("Zmień nazwę grupy");
        JButton serializeButton = new JButton("Serializuj grupę do pliku");
        JButton deserializeButton = new JButton("Deserializuj grupę z pliku");

        actionsPanel = new JPanel(new GridBagLayout());
        actionsPanel.setLayout(new GridLayout(0, 1));
        JScrollPane transfersScrollPane = new JScrollPane(actionsPanel);

        JButton addPaymentButton = new JButton("Dodaj nową płatność");

        userList = new JList<>(group.getUsers().stream().map(User::getName).toArray(String[]::new));
        JScrollPane userListScrollPane = new JScrollPane(userList);

        int padding = 10;
        setBorder(new EmptyBorder(padding, padding, padding, padding));
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(new EmptyBorder(0, 0, padding, 0));
        topPanel.add(groupNameLabel);
        topPanel.add(changeNameButton);
        topPanel.add(serializeButton);
        topPanel.add(deserializeButton);

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(new JLabel("Historia akcji"), BorderLayout.NORTH);
        historyPanel.add(transfersScrollPane, BorderLayout.CENTER);
        historyPanel.add(addPaymentButton, BorderLayout.SOUTH);

        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.add(new JLabel("Lista członków grupy"), BorderLayout.NORTH);
        usersPanel.add(userListScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, historyPanel, usersPanel);
        splitPane.setResizeWeight(0.5);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // BUTTONS
        changeNameButton.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog("Wprowadź nową nazwę grupy:");
            group.setName(newName);
            groupNameLabel.setText("Nazwa grupy: " + group.getName());
        });

        serializeButton.addActionListener(e -> {
            group.serialize();
            JOptionPane.showMessageDialog(null, "Grupa zserializowana do pliku.");
        });

        deserializeButton.addActionListener(e -> {
            group = Group.deserialize();
            assert group != null;
            groupNameLabel.setText(group.getName());
            userList.setListData(group.getUsers().stream().map(User::getName).toArray(String[]::new));
            JOptionPane.showMessageDialog(null, "Grupa zdeserializowana z pliku.");
        });

        addPaymentButton.addActionListener(e -> {
            // TODO
            showPaymentDialog(group);
        });
    }

    private void showPaymentDialog(Group group) {
        PaymentDialog dialog = new PaymentDialog(group);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.isPaymentAdded()) {
            JPanel actionPanel = new JPanel();
            actionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            actionPanel.setLayout(new GridLayout(4, 1));

            JLabel titleLabel = new JLabel("Tytuł: " +  dialog.getTitleField().getText());
            JLabel dateLabel = new JLabel("Data: " + dialog.getDateField().getText());
            JLabel amountLabel = new JLabel("Kwota: " + dialog.getAmountField().getText());
            JLabel usersLabel = new JLabel("Użytkownicy: " + String.join(", ", dialog.
                    getSelectedUsers().stream().map(User::getName).toArray(String[]::new)));

            actionPanel.add(titleLabel);
            actionPanel.add(dateLabel);
            actionPanel.add(amountLabel);
            actionPanel.add(usersLabel);

            actionPanel.setPreferredSize(new Dimension(0, 100));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridheight = 100;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.anchor = GridBagConstraints.PAGE_START;

            actionsPanel.add(actionPanel, gbc);
        }
    }
}
