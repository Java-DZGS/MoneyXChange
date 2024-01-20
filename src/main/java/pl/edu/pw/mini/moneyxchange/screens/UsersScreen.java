package pl.edu.pw.mini.moneyxchange.screens;

import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.dialogs.UserDialog;
import pl.edu.pw.mini.moneyxchange.utils.Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;


public class UsersScreen extends JPanel {
    private List<User> users;
    private final JPanel usersPanel;

    public UsersScreen() {
        users = Group.getInstance().getUsers();

        usersPanel = new JPanel(new GridBagLayout());
        JScrollPane usersScrollPlane = new JScrollPane(usersPanel);
        JButton addUserButton = new JButton("Dodaj użytkownika");

        int padding = 10;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(padding, padding, padding, padding));

        add(usersScrollPlane, BorderLayout.CENTER);
        add(addUserButton, BorderLayout.SOUTH);

        displayUsers();

        addUserButton.addActionListener(e -> showAddUserDialog());

        Group.getInstance().addListener(evt -> {
            if (!evt.getPropertyName().equals("users")) return;

            //noinspection unchecked
            users = (List<User>) evt.getNewValue();
            displayUsers();
        });
    }

    private void displayUsers() {
        usersPanel.removeAll();

        for (User user : users) {
            JPanel userPanel = user.getUserPanel();
            usersPanel.add(userPanel, Layout.getGridBagElementConstraints());
        }

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(0, 0));
        usersPanel.add(spacer, Layout.getGridBagSpacerConstraints());

        usersPanel.revalidate();
        usersPanel.repaint();
    }

    private void showAddUserDialog() {
        JDialog dialog = new UserDialog();

        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

}