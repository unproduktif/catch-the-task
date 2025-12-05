import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        // Siapkan database & tabel
        KoneksiDB.initialize();

        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}
