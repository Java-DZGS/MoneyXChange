package pl.edu.pw.mini.moneyxchange;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

public class Main extends JFrame {
    public Main() {
        super("Money X Change");

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Ekran Główny", new MainScreen());
        tabbedPane.addTab("Przelewy", new TransfersScreen());
        tabbedPane.addTab("Wykresy", new ChartsScreen());
        tabbedPane.addTab("Użytkownicy", new UsersScreen());

        // Podsumowanie
        // JPanel summaryPanel = new JPanel();
        // Dodaj komponenty do summaryPanel
        // tabbedPane.addTab("Podsumowanie", summaryPanel);

        // Historia Wydatków
        tabbedPane.addTab("Historia wydatków", new HistoryScreen());

        // Kursy Walutowe
        tabbedPane.addTab("Kursy walutowe", new ExchangeRateScreen());

        add(tabbedPane);

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());

            SwingUtilities.invokeLater(Main::new);
        } catch (UnsupportedLookAndFeelException e) {
			System.err.println("Nie udało się utworzyć aplikacji");
        }
    }
}

