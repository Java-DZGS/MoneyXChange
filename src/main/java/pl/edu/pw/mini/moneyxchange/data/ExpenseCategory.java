package pl.edu.pw.mini.moneyxchange.data;

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


    public final String label;

    ExpenseCategory(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

}
