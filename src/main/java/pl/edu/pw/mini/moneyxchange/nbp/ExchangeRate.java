package pl.edu.pw.mini.moneyxchange.nbp;

import org.javamoney.moneta.Money;

import java.util.Date;

public record ExchangeRate(Date date, Money value) {
}
