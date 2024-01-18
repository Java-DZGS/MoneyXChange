package pl.edu.pw.mini.moneyxchange.utils;

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.CurrencyStyle;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryException;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import javax.money.format.MonetaryParseException;
import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Format {
    public static final Locale PL = new Locale("pl");
    public static final CurrencyUnit CURRENCY = Monetary.getCurrency("PLN");
    public static final MonetaryAmountFormat MONETARY_FORMAT = MonetaryFormats.getAmountFormat(AmountFormatQueryBuilder.of(PL).set(CurrencyStyle.SYMBOL).set("pattern", "0.00 ¤").build());
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateLabelFormatter DATE_LABEL_FORMATTER = new DateLabelFormatter();

    static {
        CurrencyUnitBuilder.of("PLN", "złotówka").setCurrencyCode("zł").build(true);
    }

    public static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        @Override
        public Object stringToValue(String text) throws ParseException {
            return SIMPLE_DATE_FORMAT.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return SIMPLE_DATE_FORMAT.format(cal.getTime());
            }

            return "";
        }

    }

    public static class MonetaryFormatter extends JFormattedTextField.AbstractFormatter {
        @Override
        public Object stringToValue(String text) throws ParseException {
            try {
                try {
                    return Money.parse(text, MONETARY_FORMAT);
                } catch (MonetaryException ex) {
                    return Money.parse(text + " zł", MONETARY_FORMAT);
                }
            } catch (MonetaryParseException ex) {
                throw new ParseException(ex.getMessage(), ex.getErrorIndex());
            } catch (IllegalArgumentException ex) {
                throw new ParseException(ex.getMessage(), text.length());
            }
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            return MONETARY_FORMAT.format(value == null ? Money.of(0, CURRENCY) : (Money) value);
        }


    }
}
