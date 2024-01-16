package pl.edu.pw.mini.moneyxchange.data;

import pl.edu.pw.mini.moneyxchange.dialogs.UserDetailsDialog;
import pl.edu.pw.mini.moneyxchange.dialogs.UserDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// TODO
public class User implements Serializable {

    private String name;
    private int id;
    private BufferedImage image;
    private List<Expense> expenses;
    private List<Transfer> pendingTransfers;
    private List<Transfer> completedTransfers;

    private SwingPropertyChangeSupport propertyChangeSupport;

    public User(String name, int id) {
        this.name = name;
        this.id = id;
        this.expenses = new ArrayList<>();
        this.pendingTransfers = new ArrayList<>();
        this.completedTransfers = new ArrayList<>();
        this.image = null;

        this.propertyChangeSupport = new SwingPropertyChangeSupport(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        propertyChangeSupport.firePropertyChange("name", name, newName);
        name = newName;
    }

    public int getId() {
        return id;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        propertyChangeSupport.firePropertyChange("expenses", null, expenses);
    }

    public List<Transfer> getPendingTransfers() {
        return pendingTransfers;
    }

    public void addPendingTransfer(Transfer transfer) {
        pendingTransfers.add(transfer);
        propertyChangeSupport.firePropertyChange("pendingTransfers", null, pendingTransfers);
    }

    public List<Transfer> getCompletedTransfers() {
        return completedTransfers;
    }

    public void addCompletedTransfer(Transfer transfer) {
        pendingTransfers.remove(transfer);
        completedTransfers.add(transfer);
        propertyChangeSupport.firePropertyChange("pendingTransfers", null, pendingTransfers);
        propertyChangeSupport.firePropertyChange("completedTransfers", null, completedTransfers);
    }

    public void setImage(BufferedImage image) {
        propertyChangeSupport.firePropertyChange("image", this.image, image);
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public JPanel getUserPanel() {
        return new UserPanel();
    }

    public class UserPanel extends JPanel {
        JLabel nameLabel;
        JLabel imageLabel;

        public UserPanel() {
            super();
            setBorder(BorderFactory.createLineBorder(Color.BLACK));

            int padding = 10;
            setLayout(new BorderLayout());

            imageLabel = new JLabel((ImageIcon) null);
            imageLabel.setPreferredSize(new Dimension(50, 50));
            imageLabel.setMinimumSize(imageLabel.getPreferredSize());
            imageLabel.setMaximumSize(imageLabel.getPreferredSize());

            nameLabel = new JLabel(name);
            JButton detailsButton = new JButton("Szczegóły");

            if (image != null) {
                ImageIcon imageIcon = new ImageIcon(image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                imageLabel.setIcon(imageIcon);
            }

            add(imageLabel, BorderLayout.WEST);
            add(nameLabel, BorderLayout.CENTER);
            add(detailsButton, BorderLayout.EAST);

            detailsButton.addActionListener(e -> showUserDetails());

            addListener(evt -> {
                if (evt.getPropertyName().equals("name")) {
                    nameLabel.setText((String) evt.getNewValue());
                } else if (evt.getPropertyName().equals("image")) {
                    if (evt.getNewValue() != null) {
                        ImageIcon imageIcon = new ImageIcon(((BufferedImage) evt.getNewValue()).getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                        imageLabel.setIcon(imageIcon);
                    } else {
                        imageLabel.setIcon(null);
                    }
                }
            });
        }

        private void showUserDetails() {
            JDialog userDetails = new UserDetailsDialog(User.this);

            userDetails.setSize(500, 400);
            userDetails.setLocationRelativeTo(null);
            userDetails.setVisible(true);
        }

    }
}
