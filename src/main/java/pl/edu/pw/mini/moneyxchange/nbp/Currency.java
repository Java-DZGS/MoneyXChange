package pl.edu.pw.mini.moneyxchange.nbp;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

public enum Currency {
    USD("dolar amerykański"),
    AUD("dolar australijski"),
    CAD("dolar kanadyjski"),
    NZD("dolar nowozelandzki"),
    EUR("euro"),
    CHF("frank szwajcarski"),
    GBP("funt szterling"),
    UAH("hrywna");

    public final String name;
    public final CurrencyUnit currencyUnit;

    Currency(String name) {
        this.name = name;
        this.currencyUnit = Monetary.getCurrency(toString());
    }
}
