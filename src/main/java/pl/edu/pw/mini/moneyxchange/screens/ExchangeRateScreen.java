package pl.edu.pw.mini.moneyxchange.screens;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import pl.edu.pw.mini.moneyxchange.nbp.Currency;
import pl.edu.pw.mini.moneyxchange.nbp.ExchangeRate;
import pl.edu.pw.mini.moneyxchange.nbp.NBP_API;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ExchangeRateScreen extends JPanel {

    private List<ExchangeRate> exchangeData;
    private final JPanel chartPanel;
    private final JComboBox<Currency> currencySelector;
    private final JProgressBar progressBar;

    private final XYChart chart;

    public ExchangeRateScreen() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        chart = new XYChartBuilder()
                .width(800).height(600)
                .title("Kurs")
                .xAxisTitle("Data")
                .yAxisTitle("Średni kurs")
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setToolTipsEnabled(true);
        chart.getStyler().setDatePattern("yyyy-MM-dd");

        chartPanel = new XChartPanel<>(chart);
        add(chartPanel, BorderLayout.CENTER);

        currencySelector = new JComboBox<>(Currency.values());
        currencySelector.addActionListener(e -> updateChart());
        add(currencySelector, BorderLayout.NORTH);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); // Set to true for an indeterminate (spinning) progress bar
        progressBar.setVisible(false); // Initially set to invisible

        add(progressBar, BorderLayout.SOUTH);

        updateChart();
    }

    private void updateChart() {
        progressBar.setVisible(true);
        NBP_API.getCurrencyExchangeRate((Currency) Objects.requireNonNull(currencySelector.getSelectedItem()), 255)
                .thenAccept(data -> {
                    exchangeData = data;

                    List<Date> xAxis = new ArrayList<>();
                    List<Double> yAxis = new ArrayList<>();

                    exchangeData.forEach(rate -> {
                        xAxis.add(rate.date());
                        yAxis.add(rate.value().getNumber().doubleValue());
                    });

                    chart.removeSeries("Rate");
                    chart.addSeries("Rate", xAxis, yAxis);
                    repaint();
                    progressBar.setVisible(false);
                });
    }
}