package pl.edu.pw.mini.moneyxchange;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.markers.SeriesMarkers;
import pl.edu.pw.mini.moneyxchange.data.DivisionType;
import pl.edu.pw.mini.moneyxchange.data.Expense;
import pl.edu.pw.mini.moneyxchange.data.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;

public class ChartsScreen extends JPanel {

    private List<Expense> expenses;
    private JPanel chartPanel;
    private JButton filterButton;

    public ChartsScreen() {
        expenses = createDummyData(); // Initialize with dummy data

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create XChart
        Chart chart = createChart();
        chartPanel = new XChartPanel<>(chart);

        // Create filter button
        filterButton = new JButton("Filtruj...");
        filterButton.addActionListener(e -> showFilterDialog());

        // Add components to the main panel
        add(filterButton, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
    }

    private Chart createChart() {
        CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("Wydatki").xAxisTitle("Data").yAxisTitle("Kwota").build();
        chart.getStyler().setLegendVisible(false);

        Map<String, Double> aggregatedExpenses = new HashMap<>();

        // Accumulate expenses for each date
        for (Expense expense : expenses) {
            String date = expense.getDate();
            double amount = expense.getAmount();

            aggregatedExpenses.put(date, aggregatedExpenses.getOrDefault(date, 0.0) + amount);
        }

        List<String> dates = new ArrayList<>(aggregatedExpenses.keySet());
        List<Double> amounts = new ArrayList<>(aggregatedExpenses.values());

        chart.addSeries("Expenses", dates, amounts).setMarker(SeriesMarkers.CIRCLE);

        return chart;
    }


    private void showFilterDialog() {
        FilterDialog filterDialog = new FilterDialog((Frame) SwingUtilities.getWindowAncestor(this));
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);

        // Process the selected filter criteria from the dialog
        if (filterDialog.isFilterApplied()) {
            FilterCriteria filterCriteria = filterDialog.getFilterCriteria();
            // Implement filtering logic based on the selected criteria
            filterExpenses(filterCriteria);
        }
    }

    private void filterExpenses(FilterCriteria filterCriteria) {
        List<Expense> allExpenses = createDummyData();
        List<Expense> filteredExpenses = new ArrayList<>();

        for (Expense expense : allExpenses) {
            boolean dateMatch = filterCriteria.getDates() == null || filterCriteria.getDates().length == 0 || Arrays.asList(filterCriteria.getDates()).contains(expense.getDate());
            boolean participantMatch = filterCriteria.getParticipants() == null || filterCriteria.getParticipants().length == 0 || Arrays.asList(filterCriteria.getParticipants()).contains(expense.getParticipants());
            boolean payerMatch = filterCriteria.getPayer() == null || filterCriteria.getPayer().isEmpty() || filterCriteria.getPayer().equals(expense.getCreator().getName());
            if (dateMatch && participantMatch && payerMatch) {
                filteredExpenses.add(expense);
            }
        }

        if (filteredExpenses.isEmpty()) {
            // Show a warning dialog if the filtered list is empty
            JOptionPane.showMessageDialog(this, "Żadne wydatki nie pasują do nałożonych filtrów.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            expenses = filteredExpenses;

            removeAll();
            revalidate();
            repaint();
            Chart updatedChart = createChart();
            chartPanel = new XChartPanel<>(updatedChart);
            add(filterButton, BorderLayout.NORTH);
            add(chartPanel, BorderLayout.CENTER);
        }
    }



    private List<Expense> createDummyData() {
        List<Expense> dummyData = new ArrayList<>();

        dummyData.add(new Expense(new User("User1"), 50.0, List.of(new User("User2")), DivisionType.EQUAL, "Expense1", "2023-01-01"));
        dummyData.add(new Expense(new User("User2"), 30.0, List.of(new User("User1")), DivisionType.EQUAL, "Expense2", "2023-01-02"));
        dummyData.add(new Expense(new User("User3"), 40.0, List.of(new User("User1"), new User("User2")), DivisionType.EQUAL, "Expense3", "2023-01-03"));
        dummyData.add(new Expense(new User("User4"), 20.0, List.of(new User("User1")), DivisionType.EQUAL, "Expense4", "2023-01-01"));
        dummyData.add(new Expense(new User("User5"), 35.0, List.of(new User("User2")), DivisionType.EQUAL, "Expense5", "2023-01-02"));
        dummyData.add(new Expense(new User("User6"), 25.0, List.of(new User("User1"), new User("User2")), DivisionType.EQUAL, "Expense6", "2023-01-03"));
        dummyData.add(new Expense(new User("User7"), 15.0, List.of(new User("User8")), DivisionType.EQUAL, "Expense7", "2023-01-04"));
        dummyData.add(new Expense(new User("User8"), 60.0, List.of(new User("User7")), DivisionType.EQUAL, "Expense8", "2023-01-05"));
        dummyData.add(new Expense(new User("User9"), 45.0, List.of(new User("User7"), new User("User8")), DivisionType.EQUAL, "Expense9", "2023-01-06"));

        return dummyData;
    }


}

class FilterDialog extends JDialog {

    private boolean filterApplied;
    private FilterCriteria filterCriteria;

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
                if(input.isEmpty()) return new String[]{};
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

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Filter Dialog");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(new FilterDialog(frame));
//        frame.setSize(300, 200);
//        frame.setVisible(true);
//    }
}

class FilterCriteria {

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



