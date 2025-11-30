import java.awt.Color;
import java.awt.Font;

public class Theme {
    // --- PALET WARNA PASTEL ---
    public static final Color PASTEL_BG = Color.decode("#E3F2FD");       // Baby Blue
    public static final Color PASTEL_CARD = Color.decode("#FFFFFF");     // White
    public static final Color PASTEL_TEXT = Color.decode("#78909C");     // Blue Grey
    public static final Color PASTEL_TITLE = Color.decode("#FFAB91");    // Peach
    public static final Color BTN_MAIN = Color.decode("#A5D6A7");        // Pastel Green
    public static final Color BTN_HOVER = Color.decode("#81C784");       // Darker Green
    public static final Color LINK_COLOR = Color.decode("#90CAF9");      // Soft Blue
    
    // --- FONT ---
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 14);

    // --- HELPER UI: Rounded Panel ---
    // (Static inner class agar bisa dipanggil di mana saja: new Theme.RoundedPanel(...))
    public static class RoundedPanel extends javax.swing.JPanel {
        private int radius;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}