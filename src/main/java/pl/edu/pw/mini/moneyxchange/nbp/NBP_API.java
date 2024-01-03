package pl.edu.pw.mini.moneyxchange.nbp;

import com.formdev.flatlaf.json.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NBP_API {
    private static final String EXCHANGE_RATE_ENDPOINT = "https://api.nbp.pl/api/exchangerates/rates/";
    private static final SimpleDateFormat JsonDate = new SimpleDateFormat("yyyy-MM-dd");
    public static final CurrencyUnit PLN = Monetary.getCurrency("PLN");

    private static JsonObject getApiResponse(Currency currency, int count) {
        JsonObject ret = null;
        try {
            URL url = new URL(EXCHANGE_RATE_ENDPOINT + "A/" + currency.toString() + "/last/" + count + "/");
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());

                ret = JsonParser.parseReader(reader).getAsJsonObject();

                reader.close();
            } else {
                System.err.println("HTTP GET request failed: " + responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ret;
    }

    public static List<ExchangeRate> getCurrencyExchangeRate(Currency currency, int count) {
        JsonObject response = getApiResponse(currency, count);

        List<ExchangeRate> exchangeData = new ArrayList<>();

        JsonArray rates = response.getAsJsonArray("rates");
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
}
