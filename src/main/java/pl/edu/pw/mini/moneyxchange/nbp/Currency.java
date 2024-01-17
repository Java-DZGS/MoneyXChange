package pl.edu.pw.mini.moneyxchange.nbp;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

/**
 * Currency enum for communication with NBP API.
 */
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
        this.currencyUnit = Monetary.getCurrency(super.toString());
    }

    @Override
    public String toString() {
        return currencyUnit + " – " + name;
    }

    /**
     * Currency code (maybe ISO?) for communication with NBP API
     * @return currency code
     */
    public String getCode() {
        return super.name();
    }
}
