package pl.edu.pw.mini.moneyxchange.utils;

import java.awt.*;

public class Layout {
    public static GridBagConstraints getGridBagElementConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    public static GridBagConstraints getGridBagSpacerConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }
}
