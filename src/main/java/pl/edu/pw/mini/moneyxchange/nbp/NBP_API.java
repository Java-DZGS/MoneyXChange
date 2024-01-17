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
 * Klasa służąca do asynchronicznej komunikacji z API Narodowego Banku Polskiego.
 */
public class NBP_API {
    private static final String EXCHANGE_RATE_ENDPOINT = "https://api.nbp.pl/api/exchangerates/rates/";

    /**
     * Funkcja pomocnicza do asynchronicznej komunikacji z API NBP przy użyciu zapytań GET.
     * Domyślny timeout - 30 sekund.
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
     * Funkcja pobierająca asynchronicznie ostatnie {@code count} notowań waluty {@code currency} z API NBP.
     * <br><br>
     * <b>Uwaga: </b> Ze względu na błąd poza naszym zasięgiem, metoda {@code Stream::toList()} na niektórych komputerach
     * nie kończy działania. Z tego powodu zastosowaliśmy rozwiązanie zastępcze i mniej efektywne — zebranie strumienia
     * do iteratora używając {@code Stream::iterator()}, a następnie dodanie wszystkich elementów iteratora do listy.
     * @param currency waluta, której notowania chcemy
     * @param count ilość żądanych notowań
     * @return lista żądanych notowań
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
