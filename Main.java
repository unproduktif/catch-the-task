import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        KoneksiDB.initialize();

        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}
