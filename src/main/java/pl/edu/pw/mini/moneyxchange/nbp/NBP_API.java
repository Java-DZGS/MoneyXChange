package pl.edu.pw.mini.moneyxchange.nbp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.javamoney.moneta.Money;
import pl.edu.pw.mini.moneyxchange.utils.Format;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

/**
 * Class for async communication with Narodowy Bank Polski API.
 */
public class NBP_API {
    /**
     * Main exchange rate endpoint
     */
    private static final String EXCHANGE_RATE_ENDPOINT = "https://api.nbp.pl/api/exchangerates/rates/";

    /**
     * Helper function for async communication with NBP API using GET requests.
     * Default timeout - 30 seconds
     *
     * @param endpoint API endpoint to send GET request to
     * @return API JSON response
     */
    private static CompletableFuture<JsonObject> getApiResponse(String endpoint) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EXCHANGE_RATE_ENDPOINT + endpoint))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(HttpResponse::body)
                .thenApply(InputStreamReader::new)
                .thenApply(JsonParser::parseReader)
                .thenApply(JsonElement::getAsJsonObject);
    }

    /**
     * Function for async retrieval of last {@code count} exchange rates of {@code currency} from NBP API.
     * <br><br>
     * <b>Note:</b> Due to bug beyond our influence, the {@code Stream::toList()} method gets stuck on some machines.
     * To fix that we use less efficient but working solution — we collect the stream to Iterator using {@code Stream::iterator()},
     * and then we put all objects from said iterator into a List.
     *
     * @param currency currency for which to return the exchange rates
     * @param count number of exchange rates to return
     * @return list of exchange rates
     */
    public static CompletableFuture<List<ExchangeRate>> getCurrencyExchangeRate(Currency currency, int count) {
        return getApiResponse("A/" + currency.getCode() + "/last/" + count + "/")
                .thenApply(response -> response.getAsJsonArray("rates"))
                .thenApply(rates -> StreamSupport.stream(rates.spliterator(), true)
                        .map(JsonElement::getAsJsonObject)
                        .map(rate -> {
                            try {
                                Date date = Format.SIMPLE_DATE_FORMAT.parse(rate.get("effectiveDate").getAsString());
                                Money money = Money.of(rate.get("mid").getAsBigDecimal(), Format.CURRENCY);
                                return new ExchangeRate(date, money);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }).iterator())
                //HACK: Fore some reason .toList ain't working 💀
                .thenApply(iterator -> {
                    List<ExchangeRate> rates = new ArrayList<>();
                    iterator.forEachRemaining(rates::add);
                    return rates;
                });
    }
}
