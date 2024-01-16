package pl.edu.pw.mini.moneyxchange.dialogs;

import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.data.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UserDialog extends JDialog{
    private final User user;

    private JTextField nameField;
    private JTextField idField; // TODO: remove
    private JLabel imageLabel;
    private JButton addButton;

    private BufferedImage image;

    public UserDialog() {
        super((Frame) null, "Dodaj użytwkonika", true);
        this.user = null;

        build();

        // Add action listener for the "Add Transfer" button
        addButton.addActionListener(e -> {
            // Get input values
            String name = nameField.getText();
            int id;
            try {
                // Konwersja tekstu z pola tekstowego na int
                id = Integer.parseInt(idField.getText());
            } catch (NumberFormatException ex) {
                // Obsługa błędu, gdy wprowadzony tekst nie jest liczbą całkowitą
                JOptionPane.showMessageDialog(this, "To nie jest liczba całkowita. Spróbuj ponownie.", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser = new User(name, id);
            newUser.setImage(image);
            Group.getInstance().addUser(newUser);

            // Close the dialog
            dispose();
        });
    }

    public UserDialog(User user) {
        super((Frame) null, "Edytuj użytkownika", true);
        this.user = user;
        image = user.getImage();

        build();

        nameField.setText(user.getName());
        idField.setText(String.valueOf(user.getId()));

        addButton.addActionListener(e -> {
            String newName = nameField.getText();
            user.setName(newName);
            if (image != null) {
                user.setImage(image);
            }

            dispose();
        });
    }

    private void build() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2)); // TODO: Do poprawki

        inputPanel.add(new JLabel("Imię:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Id:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Obraz:"));
        imageLabel = new JLabel((ImageIcon) null); // TODO: Szczególnie to trzeba poprawić!!!
        if(image != null)
            imageLabel.setIcon(new ImageIcon(image.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        inputPanel.add(imageLabel);

        JPanel buttonPanel = new JPanel();

        JButton loadButton = new JButton("Wczytaj obraz");
        buttonPanel.add(loadButton);

        addButton = new JButton(user == null ? "Dodaj" : "Zapisz");
        buttonPanel.add(addButton);

        JButton cancelButton = new JButton("Anuluj");
        buttonPanel.add(cancelButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    image = ImageIO.read(selectedFile);
                    imageLabel.setIcon(new ImageIcon(image.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }
}
