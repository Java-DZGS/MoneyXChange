package pl.edu.pw.mini.moneyxchange;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainScreen extends JPanel {
    private Group group;

    private final JTextArea transfersTextArea;
    private final JList<String> userList;

    public MainScreen() {
        group = Group.getInstance();

        JLabel groupNameLabel = new JLabel(group.getName());
        groupNameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton changeNameButton = new JButton("Zmień nazwę grupy");
        JButton serializeButton = new JButton("Serializuj grupę do pliku");
        JButton deserializeButton = new JButton("Deserializuj grupę z pliku");

        transfersTextArea = new JTextArea(10, 30);
        transfersTextArea.setEditable(false);
        JScrollPane transfersScrollPane = new JScrollPane(transfersTextArea);

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
            groupNameLabel.setText(group.getName());
            userList.setListData(group.getUsers().stream().map(User::getName).toArray(String[]::new));
            JOptionPane.showMessageDialog(null, "Grupa zdeserializowana z pliku.");
        });

        addPaymentButton.addActionListener(e -> {
            // TODO
            transfersTextArea.append("New payment added\n");
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainScreen());
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
