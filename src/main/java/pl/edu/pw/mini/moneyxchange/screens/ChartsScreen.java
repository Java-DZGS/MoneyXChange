package pl.edu.pw.mini.moneyxchange.screens;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.markers.SeriesMarkers;
import pl.edu.pw.mini.moneyxchange.data.Expense;
import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ChartsScreen extends JPanel {


    private DateGroupingType type = DateGroupingType.DAY;
    private List<Expense> expenses;
    private final CategoryChart chart;
    private final XChartPanel<CategoryChart> chartPanel;

    public ChartsScreen() {
        expenses = Group.getInstance().getExpenses();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Create XChart
        chart = new CategoryChartBuilder().width(800).height(600).title("Wydatki").xAxisTitle("Data").yAxisTitle("Kwota").build();
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setToolTipsEnabled(true);
        chartPanel = new XChartPanel<>(chart);

        if (expenses.isEmpty()) {
            JPanel noExpensesPanel = new JPanel();
            noExpensesPanel.setLayout(new BoxLayout(noExpensesPanel, BoxLayout.Y_AXIS));
            noExpensesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noExpensesPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

            JLabel noExpensesLabel = new JLabel("Nie ma żadnych wydatków w grupie.");
            noExpensesLabel.setFont(new Font("Arial", Font.BOLD, 20));
            noExpensesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            noExpensesPanel.add(Box.createVerticalGlue());
            noExpensesPanel.add(noExpensesLabel);
            noExpensesPanel.add(Box.createVerticalGlue());

            add(noExpensesPanel);
            return;
        }

        updateChart();

        JButton filterButton = new JButton("Filtruj...");
        filterButton.addActionListener(e -> showFilterDialog());

        JComboBox<DateGroupingType> groupingTypeComboBox = new JComboBox<>(DateGroupingType.values());
        groupingTypeComboBox.setSelectedItem(type);
        groupingTypeComboBox.addActionListener(e -> {
            type = (DateGroupingType) groupingTypeComboBox.getSelectedItem();
            updateChart();
        });


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(filterButton);
        buttonPanel.add(groupingTypeComboBox);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(buttonPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(chartPanel, gbc);

        Group.getInstance().addListener(evt -> {
            if (!evt.getPropertyName().equals("expenses")) return;

            //noinspection unchecked
            expenses = (List<Expense>) evt.getNewValue();
            updateChart();
        });
    }

    private void updateChart() {
        List<String> dates = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();

        // Accumulate expenses for each day, month-year or year
        switch (type) {
            case DAY -> expenses.stream()
                    .collect(Collectors.groupingBy(Expense::getDate, Collectors.summingDouble(k -> k.getAmount().getNumber().doubleValue())))
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        try {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(entry.getKey());
                            dates.add(Format.DATE_LABEL_FORMATTER.valueToString(calendar));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        amounts.add(entry.getValue());
                    });
            case MONTH -> expenses.stream()
                    .collect(Collectors.groupingBy(Expense::getYearMonth, Collectors.summingDouble(k -> k.getAmount().getNumber().doubleValue())))
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        dates.add(entry.getKey().toString());
                        amounts.add(entry.getValue());
                    });
            case YEAR -> expenses.stream()
                    .collect(Collectors.groupingBy(Expense::getYear, Collectors.summingDouble(k -> k.getAmount().getNumber().doubleValue())))
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        dates.add(entry.getKey().toString());
                        amounts.add(entry.getValue());
                    });
        }

        try {
            chart.updateCategorySeries("Expenses", dates, amounts, null);
        } catch (IllegalArgumentException e) {
            chart.addSeries("Expenses", dates, amounts).setMarker(SeriesMarkers.CIRCLE);
        }

        chartPanel.repaint();
    }


    private void showFilterDialog() {
        FilterDialog filterDialog = new FilterDialog((Frame) SwingUtilities.getWindowAncestor(this));
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);

        // Process the selected filter criteria from the dialog
        if (filterDialog.isFilterApplied()) {
            FilterDialog.FilterCriteria filterCriteria = filterDialog.getFilterCriteria();
            // Implement filtering logic based on the selected criteria
            filterExpenses(filterCriteria);
        }
    }

    private void filterExpenses(FilterDialog.FilterCriteria filterCriteria) {
        List<Expense> allExpenses = Group.getInstance().getExpenses();
        List<Expense> filteredExpenses = new ArrayList<>();

        for (Expense expense : allExpenses) {
            boolean dateMatch = filterCriteria.getDates() == null || filterCriteria.getDates().length == 0 || Arrays.asList(filterCriteria.getDates()).contains(expense.getDate());
            boolean participantMatch = filterCriteria.getParticipants() == null || filterCriteria.getParticipants().length == 0 || Arrays.asList(filterCriteria.getParticipants()).contains(expense.getParticipants());
            boolean payerMatch = filterCriteria.getPayer() == null || filterCriteria.getPayer().isEmpty() || filterCriteria.getPayer().equals(expense.getPayer().getName());
            if (dateMatch && participantMatch && payerMatch) {
                filteredExpenses.add(expense);
            }
        }

        if (filteredExpenses.isEmpty()) {
            // Show a warning dialog if the filtered list is empty
            JOptionPane.showMessageDialog(this, "Żadne wydatki nie pasują do nałożonych filtrów.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            expenses = filteredExpenses;

            updateChart();
        }
    }

    static class FilterDialog extends JDialog {

        private boolean filterApplied;
        private final FilterCriteria filterCriteria;

        public FilterDialog(Frame owner) {
            super(owner, "Opcje filtrowania", true);

            filterApplied = false;
            filterCriteria = new FilterCriteria();

            setLayout(new GridLayout(4, 2));

            JLabel dateLabel = new JLabel("Daty (oddzielone przecinkiem): ");
            JTextField dateField = new JTextField();
            add(dateLabel);
            add(dateField);

            JLabel participantLabel = new JLabel("Uczestnicy (oddzielone przecinkiem): ");
            JTextField participantField = new JTextField();
            add(participantLabel);
            add(participantField);

            JLabel payerLabel = new JLabel("Płacący: ");
            JTextField payerField = new JTextField();
            add(payerLabel);
            add(payerField);

            JButton applyButton = new JButton("Apply Filter");
            applyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Set filter criteria based on user input
                    filterCriteria.setDates(parseCSV(dateField.getText()));
                    filterCriteria.setParticipants(parseCSV(participantField.getText()));
                    filterCriteria.setPayer(payerField.getText());
                    filterApplied = true;
                    setVisible(false);
                }

                private String[] parseCSV(String input) {
                    if (input.isEmpty()) return new String[]{};
                    return input.split("\\s*,\\s*");
                }
            });

            add(applyButton);

            pack();
        }

        public boolean isFilterApplied() {
            return filterApplied;
        }

        public FilterCriteria getFilterCriteria() {
            return filterCriteria;
        }

        public static class FilterCriteria {

            private String[] dates;
            private String[] participants;
            private String payer;

            public String[] getDates() {
                return dates;
            }

            public void setDates(String[] dates) {
                this.dates = dates;
            }

            public String[] getParticipants() {
                return participants;
            }

            public void setParticipants(String[] participants) {
                this.participants = participants;
            }

            public String getPayer() {
                return payer;
            }

            public void setPayer(String payer) {
                this.payer = payer;
            }
        }
    }
}

enum DateGroupingType {
    DAY("Dzień"),
    MONTH("Miesiąc"),
    YEAR("Rok");


    public final String label;

    DateGroupingType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

