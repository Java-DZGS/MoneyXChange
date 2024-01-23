package pl.edu.pw.mini.moneyxchange.dialogs;

import org.javamoney.moneta.Money;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import pl.edu.pw.mini.moneyxchange.data.Expense;
import pl.edu.pw.mini.moneyxchange.data.ExpenseCategory;
import pl.edu.pw.mini.moneyxchange.data.MoneyAction;
import pl.edu.pw.mini.moneyxchange.utils.Format;
import pl.edu.pw.mini.moneyxchange.utils.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class FilterDialog extends JDialog {

    private boolean filterApplied;
    private final FilterCriteria filterCriteria;

    public FilterDialog(Frame owner) {
        super(owner, "Opcje filtrowania", true);

        filterApplied = false;
        filterCriteria = new FilterCriteria();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel dateLabel = new JLabel("Data:");
        JLabel fromLabel = new JLabel("od");
        UtilDateModel fromDateModel = new UtilDateModel();
        JDatePanelImpl fromDatePanel = new JDatePanelImpl(fromDateModel, Format.DATE_PICKER_PROPERTIES);
        JDatePickerImpl fromDatePicker = new JDatePickerImpl(fromDatePanel, Format.DATE_LABEL_FORMATTER);
        fromDatePicker.setPreferredSize(new Dimension(110, 25));
        JLabel toLabel = new JLabel("do");
        UtilDateModel toDateModel = new UtilDateModel();
        JDatePanelImpl toDatePanel = new JDatePanelImpl(toDateModel, Format.DATE_PICKER_PROPERTIES);
        JDatePickerImpl toDatePicker = new JDatePickerImpl(toDatePanel, Format.DATE_LABEL_FORMATTER);
        toDatePicker.setPreferredSize(new Dimension(110, 25));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(dateLabel, gbc);
        gbc.gridx = 1;
        add(fromLabel, gbc);
        gbc.gridx = 2;
        add(fromDatePicker, gbc);
        gbc.gridx = 3;
        add(toLabel, gbc);
        gbc.gridx = 4;
        add(toDatePicker, gbc);

        JLabel amountLabel = new JLabel("Kwota:");
        JLabel fromAmountLabel = new JLabel("od");
        JFormattedTextField fromAmountField = new JFormattedTextField(new Format.MonetaryFormatter());
        SwingUtils.addChangeListener(fromAmountField, e -> handleAmountFieldTextChange(fromAmountField));
        JLabel toAmountLabel = new JLabel("do");
        JFormattedTextField toAmountField = new JFormattedTextField(new Format.MonetaryFormatter());
        SwingUtils.addChangeListener(toAmountField, e -> handleAmountFieldTextChange(toAmountField));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(amountLabel, gbc);
        gbc.gridx = 1;
        add(fromAmountLabel, gbc);
        gbc.gridx = 2;
        add(fromAmountField, gbc);
        gbc.gridx = 3;
        add(toAmountLabel, gbc);
        gbc.gridx = 4;
        add(toAmountField, gbc);

        JLabel titleLabel = new JLabel("Tytuł:");
        JTextField titleField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(titleLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(titleField, gbc);

        JLabel categoryLabel = new JLabel("Kategoria:");
        JComboBox<ExpenseCategory> categoryComboBox = new JComboBox<>(ExpenseCategory.values());
        JCheckBox categoryCheckBox = new JCheckBox();
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(categoryLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(categoryCheckBox, gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(categoryComboBox, gbc);

        categoryCheckBox.setSelected(false);
        categoryComboBox.setEnabled(false);
        categoryCheckBox.addActionListener(e -> {
            categoryComboBox.setEnabled(categoryCheckBox.isSelected());
        });

        JButton applyButton = new JButton("Zatwierdź");
        applyButton.addActionListener(e -> {
            filterCriteria.setFromDate((Date) fromDatePicker.getModel().getValue());
            filterCriteria.setToDate((Date) toDatePicker.getModel().getValue());
            filterCriteria.setFromAmount((Money) fromAmountField.getValue());
            filterCriteria.setToAmount((Money) toAmountField.getValue());
            filterCriteria.setTitleKeyword(titleField.getText());
            if (categoryCheckBox.isSelected())
                filterCriteria.setCategory((ExpenseCategory) categoryComboBox.getSelectedItem());

            if (!filterCriteria.isFilterValid()) {
                JOptionPane.showMessageDialog(
                        null, "Podaj poprawne dane", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            filterApplied = true;
            dispose();
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 5;
        add(applyButton, gbc);

        pack();
    }

    private void handleAmountFieldTextChange(JFormattedTextField textField) {
        try {
            textField.commitEdit();
            textField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        } catch (Exception ex) {
            textField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
    }


    public boolean isFilterApplied() {
        return filterApplied;
    }

    public FilterCriteria getFilterCriteria() {
        return filterCriteria;
    }

    public static class FilterCriteria {
        private Date fromDate;
        private Date toDate;
        private Money fromAmount;
        private Money toAmount;
        private ExpenseCategory category;
        private String keyword;

        public void setFromDate(Date date) {
            if (date == null)
                return;

            // samo `fromDate = date` ustawi wybrany w kalendarzu dzień, ale aktualną godzinę,
            // to niepoprawne w filtrowaniu
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            fromDate = calendar.getTime();
        }

        public void setToDate(Date date) {
            if (date == null)
                return;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);

            toDate = calendar.getTime();
        }

        public void setFromAmount(Money money) {
            fromAmount = money;
        }

        public void setToAmount(Money money) {
            toAmount = money;
        }

        public void setTitleKeyword(String text) {
            keyword = text;
        }

        public void setCategory(ExpenseCategory category) {
            this.category = category;
        }

        public boolean isFilterValid() {
            return (fromDate == null || toDate == null || !fromDate.after(toDate)) &&
                    (fromAmount == null || toAmount == null || fromAmount.isLessThanOrEqualTo(toAmount));
        }

        private boolean checkDate(Date date) {
            return (fromDate == null || !fromDate.after(date)) &&
                    (toDate == null || !toDate.before(date));
        }

        private boolean checkAmount(Money money) {
            return (fromAmount == null || fromAmount.isLessThanOrEqualTo(money)) &&
                    (toAmount == null || toAmount.isZero() || toAmount.isGreaterThanOrEqualTo(money));
        }

        private boolean checkTitle(String text) {
            return text.toLowerCase().contains(keyword.toLowerCase());
        }

        private boolean checkCategory(ExpenseCategory category)
        {
            return this.category == null || category == this.category;
        }

        public boolean applyFilter(MoneyAction action) {
            return checkDate(action.getDate())
                    && checkAmount(action.getAmount())
                    && checkTitle(action.getName())
                    && (!(action instanceof Expense) || checkCategory(((Expense)action).getCategory()));
        }

    }

}
