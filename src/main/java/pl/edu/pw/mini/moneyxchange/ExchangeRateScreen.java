package pl.edu.pw.mini.moneyxchange;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.knowm.xchart.*;
import org.knowm.xchart.internal.chartpart.Chart;

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
    public static final CurrencyUnit PLN = Monetary.getCurrency("PLN");
    private static final SimpleDateFormat JsonDate = new SimpleDateFormat("yyyy-MM-dd");

    private List<ExchangeRate> exchangeData;

    private JPanel chartPanel;

    public ExchangeRateScreen() {
        exchangeData = createDummyData(); // Initialize with dummy data

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

    private List<ExchangeRate> createDummyData() {
        List<ExchangeRate> exchangeData = new ArrayList<>();

        JsonObject object;
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream("data.json"))) {
            object = JsonParser.parseReader(isr).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonArray rates = object.getAsJsonArray("rates");

        for(var r : rates) {
            JsonObject rate = (JsonObject) r;
            Date date;
            Money money;
            try {
                date = JsonDate.parse(rate.get("effectiveDate").getAsString());
                money = Money.of(rate.get("mid").getAsBigDecimal(), PLN);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            exchangeData.add(new ExchangeRate(date, money));
        }

        return exchangeData;
    }

    private static class ExchangeRate {
        Date date;
        Money value;

        public ExchangeRate(Date date, Money value) {
            this.date = date;
            this.value = value;
        }
    }
}