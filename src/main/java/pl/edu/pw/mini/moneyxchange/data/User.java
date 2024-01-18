package pl.edu.pw.mini.moneyxchange.data;

import pl.edu.pw.mini.moneyxchange.dialogs.UserDetailsDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.*;
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
	 * Static field to track the next available user ID.
     */
    private static int ID = 0;

    /**
     * The unique identifier of the user.
     */
    private final int id;

    /**
     * The profile image of the user.
     */
    transient private BufferedImage image;
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
     * Displays detailed information about the user in a dialog.
     */
    public void showUserDetails() {
        JDialog userDetails = new UserDetailsDialog(User.this);

        userDetails.setSize(500, 400);
        userDetails.setLocationRelativeTo(null);
        userDetails.setVisible(true);
    }

    /**
     * Method required for {@code BufferedImage} serialization
     *
     * @param out output stream
     * @throws IOException exception from {@code ObjectOutputStream.defaultWriteObject()}
     */
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeBoolean(image != null);
        if(image != null)
            ImageIO.write(image, "png", out); // png is lossless
    }


    /**
     * Method required for {@code BufferedImage} deserialization
     *
     * @param in input stream
     * @throws IOException exception from {@code ObjectOutputStream.defaultReadObject()}
     * @throws ClassNotFoundException exception from {@code ObjectOutputStream.defaultReadObject()}
     */
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final boolean hasImage = in.readBoolean();
        if(hasImage)
            image = ImageIO.read(in);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name=" + name +
                '}';
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
}
