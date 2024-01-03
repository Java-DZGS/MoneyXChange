package pl.edu.pw.mini.moneyxchange.nbp;

import org.javamoney.moneta.Money;

import java.util.Date;

public class ExchangeRate {
    public final Date date;
    public final Money value;

    public ExchangeRate(Date date, Money value) {
        this.date = date;
        this.value = value;
    }
}
