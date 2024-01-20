package pl.edu.pw.mini.moneyxchange.data;

import org.javamoney.moneta.Money;

import javax.swing.*;
import java.util.Date;

public interface MoneyAction {
    Date getDate();
    Money getAmount();

    JPanel getPanel();
}
