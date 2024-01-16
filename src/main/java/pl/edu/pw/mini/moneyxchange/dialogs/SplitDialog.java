package pl.edu.pw.mini.moneyxchange.dialogs;

import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.data.User;
import pl.edu.pw.mini.moneyxchange.utils.splitters.*;
import pl.edu.pw.mini.moneyxchange.utils.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

// todo: moze w przyszłosci zrobić z tego część okienka, zamiast nowego
public class SplitDialog extends JDialog {
    enum DivisionType {
        EQUAL, EXACT, PERCENTAGE, SHARES
    }

    private ISplitter splitter;
    private DivisionType divisionType;
    private final JPanel dialogPanel;
    private final JPanel inputPanel;
    private final JPanel feedbackPanel;
    private final JComboBox<String> divisionTypeComboBox;
    private Map<User, Money> outputMap;

    public Map<User, Money> getOutputMap() {
        return outputMap;
    }

    private final List<User> users;
    private final Money expenseAmount;
    private double leftAmount;

    private boolean resultOK;

    public SplitDialog(List<User> users, Money amount) {
        resultOK = false;
        this.users = users;
        this.expenseAmount = amount;
        divisionType = DivisionType.EQUAL;
        outputMap = new HashMap<>();

        setSplitter();

        {
            dialogPanel = new JPanel(new GridBagLayout());
            add(dialogPanel);

            divisionTypeComboBox = new JComboBox<>(
                    // convert enum values to array of strings
                    Stream.of(DivisionType.values())
                            .map(DivisionType::name)
                            .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()) // capitalize only first letter
                            .toArray(String[]::new));

            divisionTypeComboBox.addActionListener(e -> {
                divisionType = DivisionType.valueOf(
                        Objects.requireNonNull(divisionTypeComboBox
                                        .getSelectedItem())
                                .toString()
                                .toUpperCase());
                setSplitter();
                drawInputPanel();
                drawFeedbackPanel();
            });

            JPanel divisionTypePanel = new JPanel();
            divisionTypePanel.add(divisionTypeComboBox);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weighty = 0.1;
            dialogPanel.add(divisionTypePanel, gbc);

            feedbackPanel = new JPanel();
            gbc.gridy = 1;
            dialogPanel.add(feedbackPanel, gbc);

            inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setLayout(new GridLayout(0, 1, 10, 10));
            JScrollPane scrollPane = new JScrollPane(inputPanel);
            gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridy = 2;
            gbc.weighty = 3.0;
            dialogPanel.add(scrollPane, gbc);

            drawInputPanel();

            JButton okButton = new JButton("Zatwierdź");
            gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridy = 3;
            gbc.weighty = 0.1;
            okButton.addActionListener(e -> {
                resultOK = true;
                if (splitter.isReadyToSplit()) {
                    calculateSplits();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(
                            null, "Błędne dane", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            });
            dialogPanel.add(okButton, gbc);

            drawFeedbackPanel();
        }
    }

    private void setSplitter() {
        switch (divisionType) {
            case EQUAL -> splitter = new EqualSplitter(expenseAmount);
            case EXACT -> splitter = new ExactSplitter(expenseAmount);
            case PERCENTAGE -> splitter = new PercentageSplitter(expenseAmount);
            case SHARES -> splitter = new SharesSplitter(expenseAmount);
        }
    }

    private void drawFeedbackPanel() {
        feedbackPanel.removeAll();

        JLabel label = new JLabel(splitter.getFeedback());
        feedbackPanel.add(label);

        feedbackPanel.revalidate();
        feedbackPanel.repaint();
    }

    private void drawInputPanel() {
        inputPanel.removeAll();

        for (User user : users) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setLayout(new GridLayout(1, 2));
            JLabel label = new JLabel(user.getName());

            JComponent comp = getSplitInputComponent(user);
            panel.add(label);
            panel.add(comp);
            inputPanel.add(panel);
        }

        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private JComponent getSplitInputComponent(User user) {
        if (Objects.requireNonNull(divisionType) == DivisionType.EQUAL) {
            // todo: make textboxes not require currency unit written out
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(true);
            splitter.addUser(user, "");
            checkBox.addActionListener(e -> handleCheckBoxChange(checkBox, user));
            return checkBox;
        }

        JTextField textField = new JTextField();
        handleSplitTextInputChange(textField, user);
        SwingUtils.addChangeListener(textField, e -> handleSplitTextInputChange(textField, user));
        return textField;
    }

    private void handleCheckBoxChange(JCheckBox checkBox, User user) {
        if (checkBox.isSelected())
            splitter.addUser(user, "");
        else
            splitter.removeUser(user);

        drawFeedbackPanel();
    }

    private void handleSplitTextInputChange(JTextField textField, User user) {
        boolean validationResultOK = splitter.addUser(user, textField.getText());

        if (validationResultOK)
            textField.setBorder(BorderFactory.createEmptyBorder());
        else
            textField.setBorder(BorderFactory.createLineBorder(Color.RED));

        drawFeedbackPanel();
    }

    private void calculateSplits() {
        outputMap = splitter.split();
    }

    public boolean isResultOK() {
        return resultOK;
    }
}
