package pl.edu.pw.mini.moneyxchange.dialogs;

import org.javamoney.moneta.Money;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import pl.edu.pw.mini.moneyxchange.data.Expense;
import pl.edu.pw.mini.moneyxchange.data.MoneyAction;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class FilterDialog extends JDialog {

    private boolean filterApplied;
    private FilterCriteria filterCriteria;

    public FilterDialog(Frame owner) {
        super(owner, "Opcje filtrowania", true);

        filterApplied = false;
        filterCriteria = new FilterCriteria();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // First Row
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

        // Second Row
        JLabel amountLabel = new JLabel("Kwota:");
        // todo: textFieldy mają być formatted textfieldami,
        //  na razie zostawiam bo tamten branch jest niezmergowany
        JLabel fromAmountLabel = new JLabel("od");
        JTextField fromAmountField = new JTextField(6);
        JLabel toAmountLabel = new JLabel("do");
        JTextField toAmountField = new JTextField(6);
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

        // Third Row
        JLabel titleLabel = new JLabel("Tytuł:");
        JTextField titleField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(titleLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 4; // Span across four columns
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(titleField, gbc);

        JButton applyButton = new JButton("Zatwierdź");
        applyButton.addActionListener(e -> {
            filterCriteria.setFromDate((Date) fromDatePicker.getModel().getValue());
            filterCriteria.setToDate((Date) toDatePicker.getModel().getValue());
            filterCriteria.setFromAmount(fromAmountField.getText());
            filterCriteria.setToAmount(toAmountField.getText());
            filterCriteria.setTitleKeyword(titleField.getText());

            if (!filterCriteria.isFilterValid()) {
                JOptionPane.showMessageDialog(
                        null, "Podaj poprawne dane", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            filterApplied = true;
            dispose();
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 5;
        add(applyButton, gbc);

        pack();
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

        private String keyword;

        public void setFromDate(Date date) {
            if (date == null)
                return;

            // samo `fromDate = date` ustawi dobry dzień, ale godzinę aktualną,
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

        public void setFromAmount(String text) {
            //todo
            fromAmount = Money.of(1, Format.CURRENCY);
        }

        public void setToAmount(String text) {
            // todo
            toAmount = Money.of(100, Format.CURRENCY);
        }

        public void setTitleKeyword(String text) {
            keyword = text;
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
                    (toAmount == null || toAmount.isGreaterThanOrEqualTo(money));
        }

        private boolean checkTitle(String text) {
            return text.contains(keyword);
        }

        public boolean applyFilter(MoneyAction action) {
            return checkDate(action.getDate())
                    && checkAmount(action.getAmount())
                    && (!(action instanceof Expense) || checkTitle(((Expense) action).getName()));
        }

    }

}

/*
    private void applyAmountFilter() {
        String keyword = JOptionPane.showInputDialog(this, "Enter Amount Keyword:");
        if (keyword != null) {
            try {
                double amount = Double.parseDouble(keyword);
                List<Transfer> filteredTransfers = new ArrayList<>();
                for (Transfer transfer : transfers) {
                    if (transfer.getAmount().getNumber().doubleValue() == amount) {
                        filteredTransfers.add(transfer);
                    }
                }
                transfers = filteredTransfers;
                displayTransfers();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.");
            }
        }
    }
}
 */



