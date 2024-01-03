package pl.edu.pw.mini.moneyxchange;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.knowm.xchart.*;
import org.knowm.xchart.internal.chartpart.Chart;
import pl.edu.pw.mini.moneyxchange.nbp.Currency;
import pl.edu.pw.mini.moneyxchange.nbp.ExchangeRate;
import pl.edu.pw.mini.moneyxchange.nbp.NBP_API;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class ExchangeRateScreen extends JPanel {

    private List<ExchangeRate> exchangeData;

    private JPanel chartPanel;

    public ExchangeRateScreen() {
        exchangeData = NBP_API.getCurrencyExchangeRate(Currency.CAD, 255); // Initialize with dummy data

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create XChart
        Chart chart = createChart();
        chartPanel = new XChartPanel<>(chart);

        // Add components to the main panel
        add(chartPanel, BorderLayout.CENTER);
    }

    private Chart createChart() {
        XYChart chart = new XYChartBuilder()
                .width(800).height(600)
                .title("Kurs")
                .xAxisTitle("Data")
                .yAxisTitle("Średni kurs")
                .build();
        chart.getStyler().setLegendVisible(false);

        List<Date> xAxis = new ArrayList<>();
        List<Double> yAxis = new ArrayList<>();

        exchangeData.forEach(rate -> {
            xAxis.add(rate.date);
            yAxis.add(rate.value.getNumber().doubleValue());
        });

        chart.addSeries("EUR", xAxis, yAxis);

        return chart;
    }
}