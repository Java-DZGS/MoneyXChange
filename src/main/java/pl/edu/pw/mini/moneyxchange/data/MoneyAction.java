package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;

import javax.swing.*;
import java.util.Comparator;
import java.util.Date;

public interface MoneyAction extends Comparator<MoneyAction> {
    Date getDate();
    Money getAmount();

    String getName();
    JPanel getPanel();

    @Override
    default int compare(MoneyAction o1, MoneyAction o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}
