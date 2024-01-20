package pl.edu.pw.mini.moneyxchange.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the category of an expense.
 */
public enum ExpenseCategory {
    ENTERTAINMENT("Rozrywka"),
    FOOD("Żywność"),
    TRANSPORT("Transport"),
    HEALTH("Zdrowie"),
    EDUCATION("Edukacja"),
    HOUSING("Mieszkanie"),
    CLOTHING("Odzież"),
    TECHNOLOGY("Technologia"),
    TRAVEL("Podróże"),
    UTILITIES("Opłaty za media"),
    GIFTS("Prezenty"),
    SUBSCRIPTIONS("Subskrypcje"),
    OTHER("Inne");

    private static final Map<String, ExpenseCategory> BY_LABEL = new HashMap<>();

    static {
        for (ExpenseCategory category: values()) {
            BY_LABEL.put(category.label, category);
        }
    }

    public final String label;

    ExpenseCategory(String label) {
        this.label = label;
    }

    public static String[] labels() {
        return BY_LABEL.keySet().toArray(new String[0]);
    }

    public static ExpenseCategory valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
