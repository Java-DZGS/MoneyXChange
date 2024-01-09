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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NBP_API {
    private static final String EXCHANGE_RATE_ENDPOINT = "https://api.nbp.pl/api/exchangerates/rates/";

    private static CompletableFuture<JsonObject> getApiResponse(String endpoint) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EXCHANGE_RATE_ENDPOINT + endpoint))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
//                .thenApply(inputStreamHttpResponse -> {
//                    System.out.println(inputStreamHttpResponse.statusCode());
//                    return inputStreamHttpResponse;
//                })
                //TODO: Temporary, just for testing
                .thenApply(v -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return v;
                })
                .thenApply(HttpResponse::body)
                .thenApply(InputStreamReader::new)
                .thenApply(JsonParser::parseReader)
                .thenApply(JsonElement::getAsJsonObject);
    }

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
                        })
                        .collect(Collectors.toList()));
    }
}
