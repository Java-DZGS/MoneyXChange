package pl.edu.pw.mini.moneyxchange.utils;

import org.javamoney.moneta.format.CurrencyStyle;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Format {
    public static final Locale PL = new Locale("pl");
    public static final CurrencyUnit CURRENCY = Monetary.getCurrency("PLN");
    public static final MonetaryAmountFormat MONETARY_FORMAT = MonetaryFormats.getAmountFormat(AmountFormatQueryBuilder.of(PL).set(CurrencyStyle.SYMBOL).set("pattern", "0.00 ¤").build());
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
}
