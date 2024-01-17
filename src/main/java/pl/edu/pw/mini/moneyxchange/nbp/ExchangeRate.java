package pl.edu.pw.mini.moneyxchange.nbp;

import org.javamoney.moneta.Money;

import java.util.Date;

/**
 * Record reprezentujący jedno notowanie waluty
 * @param date Data notowania
 * @param value Wartość notowania
 */
public record ExchangeRate(Date date, Money value) {
}
