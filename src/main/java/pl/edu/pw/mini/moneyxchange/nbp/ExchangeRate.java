package pl.edu.pw.mini.moneyxchange.nbp;

import org.javamoney.moneta.Money;

import java.util.Date;

/**
 * Record representing one exchange rate
 * @param date Date of the exchange rate
 * @param value Value of the exchange rate
 */
public record ExchangeRate(Date date, Money value) {
}
