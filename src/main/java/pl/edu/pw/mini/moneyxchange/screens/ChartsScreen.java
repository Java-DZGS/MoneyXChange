package pl.edu.pw.mini.moneyxchange.screens;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.markers.SeriesMarkers;
import pl.edu.pw.mini.moneyxchange.data.Expense;
import pl.edu.pw.mini.moneyxchange.data.Group;
import pl.edu.pw.mini.moneyxchange.dialogs.FilterDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ChartsScreen extends JPanel {

    private List<Expense> expenses;
    private final CategoryChart chart;

    public ChartsScreen() {
        expenses = Group.getInstance().getExpenses();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create XChart
        chart = new CategoryChartBuilder().width(800).height(600).title("Wydatki").xAxisTitle("Data").yAxisTitle("Kwota").build();
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setDatePattern("yyyy-MM-dd");
        chart.getStyler().setToolTipsEnabled(true);
        XChartPanel<CategoryChart> chartPanel = new XChartPanel<>(chart);

        updateChart();

        // Create filter button
        JButton filterButton = new JButton("Filtruj...");
        filterButton.addActionListener(e -> showFilterDialog());

        // Add components to the main panel
        add(filterButton, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);

        Group.getInstance().addListener(evt -> {
            if (!evt.getPropertyName().equals("expenses")) return;

            //noinspection unchecked
            expenses = (List<Expense>) evt.getNewValue();
            updateChart();
        });
    }

    private void updateChart() {
        List<Date> dates = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();

        // Accumulate expenses for each date
        expenses.stream()
                .collect(Collectors.groupingBy(Expense::getDate, Collectors.summingDouble(k -> k.getAmount().getNumber().doubleValue())))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    dates.add(entry.getKey());
                    amounts.add(entry.getValue());
                });

        // todo: doesnt work if expenses is empty
        try {
            chart.updateCategorySeries("Expenses", dates, amounts, null);
        } catch (IllegalArgumentException e) {
            chart.addSeries("Expenses", dates, amounts).setMarker(SeriesMarkers.CIRCLE);
        }
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
}