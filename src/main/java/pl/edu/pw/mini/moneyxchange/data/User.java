package pl.edu.pw.mini.moneyxchange.data;

import pl.edu.pw.mini.moneyxchange.dialogs.UserDetailsDialog;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user with basic information and functionality.
 */
public class User implements Serializable {

    /**
     * The name of the user.
     */
    private String name;

    /**
     * A unique identifier for the user.
     */
    private static int ID = 0;

    /**
     * The unique identifier of the user.
     */
    private final int id;

    /**
     * The profile image of the user.
     */
    private BufferedImage image;

    /**
     * The list of expenses associated with the user.
     */
    private List<Expense> expenses;

    /**
     * The list of pending transfers associated with the user.
     */
    private List<Transfer> pendingTransfers;

    /**
     * The list of completed transfers associated with the user.
     */
    private List<Transfer> completedTransfers;

    /**
     * Support for property change events.
     */
    private SwingPropertyChangeSupport propertyChangeSupport;

    /**
     * Constructs a new user with the specified name.
     *
     * @param name The name of the user.
     */
    public User(String name) {
        this.id = ID++;
        this.name = name;
        this.expenses = new ArrayList<>();
        this.pendingTransfers = new ArrayList<>();
        this.completedTransfers = new ArrayList<>();
        this.image = null;

        this.propertyChangeSupport = new SwingPropertyChangeSupport(this);
    }

    /**
     * Gets the name of the user.
     *
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param newName The new name for the user.
     */
    public void setName(String newName) {
        propertyChangeSupport.firePropertyChange("name", name, newName);
        name = newName;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return The user's identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the list of expenses associated with the user.
     *
     * @return The list of expenses.
     */
    public List<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Adds a new expense to the user's list of expenses.
     *
     * @param expense The expense to be added.
     */
    public void addExpense(Expense expense) {
        expenses.add(expense);
        propertyChangeSupport.firePropertyChange("expenses", null, expenses);
    }

    /**
     * Gets the list of pending transfers associated with the user.
     *
     * @return The list of pending transfers.
     */
    public List<Transfer> getPendingTransfers() {
        return pendingTransfers;
    }

    /**
     * Adds a new pending transfer to the user's list of pending transfers.
     *
     * @param transfer The pending transfer to be added.
     */
    public void addPendingTransfer(Transfer transfer) {
        pendingTransfers.add(transfer);
        propertyChangeSupport.firePropertyChange("pendingTransfers", null, pendingTransfers);
    }

    /**
     * Gets the list of completed transfers associated with the user.
     *
     * @return The list of completed transfers.
     */
    public List<Transfer> getCompletedTransfers() {
        return completedTransfers;
    }

    /**
     * Adds a new completed transfer to the user's list of completed transfers.
     *
     * @param transfer The completed transfer to be added.
     */
    public void addCompletedTransfer(Transfer transfer) {
        pendingTransfers.remove(transfer);
        completedTransfers.add(transfer);
        propertyChangeSupport.firePropertyChange("pendingTransfers", null, pendingTransfers);
        propertyChangeSupport.firePropertyChange("completedTransfers", null, completedTransfers);
    }

    /**
     * Sets the user's profile image.
     *
     * @param image The new image for the user.
     */
    public void setImage(BufferedImage image) {
        propertyChangeSupport.firePropertyChange("image", this.image, image);
        this.image = image;
    }

    /**
     * Gets the user's profile image.
     *
     * @return The user's profile image.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Adds a property change listener to the user.
     *
     * @param listener The listener to be added.
     */
    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Gets a JPanel representing the user for display purposes.
     *
     * @return A JPanel representing the user.
     */
    public JPanel getUserPanel() {
        return new UserPanel();
    }

    /**
     * Inner class representing a panel displaying user information.
     */
    public class UserPanel extends JPanel {
        JLabel nameLabel;
        JLabel imageLabel;

        /**
         * Constructs a new UserPanel for displaying user information.
         */
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
    }

    /**
     * Displays detailed information about the user in a dialog.
     */
    public void showUserDetails() {
        JDialog userDetails = new UserDetailsDialog(User.this);

        userDetails.setSize(500, 400);
        userDetails.setLocationRelativeTo(null);
        userDetails.setVisible(true);
    }
}
