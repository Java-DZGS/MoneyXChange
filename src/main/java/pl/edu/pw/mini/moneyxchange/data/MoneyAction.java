package pl.edu.pw.mini.moneyxchange.data;

import javax.swing.*;
import java.util.Date;

public interface MoneyAction {
    Date getDate();

    JPanel getPanel();
}
