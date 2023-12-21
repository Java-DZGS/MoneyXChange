package pl.edu.pw.mini.moneyxchange;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

public class Main extends JFrame {
    public Main() {
        super("Money X Change");

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Ekran Główny", new MainScreen());

        // Przelewy
        JPanel przelewyPanel = new JPanel();
        // Dodaj komponenty do przelewyPanel
        tabbedPane.addTab("Przelewy", przelewyPanel);

        // Wykresy
        JPanel wykresyPanel = new JPanel();
        // Dodaj komponenty do wykresyPanel
        tabbedPane.addTab("Wykresy", wykresyPanel);

        // Podsumowanie
        JPanel podsumowaniePanel = new JPanel();
        // Dodaj komponenty do podsumowaniePanel
        tabbedPane.addTab("Podsumowanie", podsumowaniePanel);

        // Historia Wydatków
        JPanel historiaPanel = new JPanel();
        // Dodaj komponenty do historiaPanel
        tabbedPane.addTab("Historia Wydatków", historiaPanel);

        // Kursy Walutowe
        JPanel kursyPanel = new JPanel();
        // Dodaj komponenty do kursyPanel
        tabbedPane.addTab("Kursy Walutowe", kursyPanel);

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

