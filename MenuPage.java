import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuPage extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContainer;
    private LeaderboardPanel leaderboardPanel;

    public MenuPage() {
        setTitle("Catch The Task - Menu Utama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 600));

        // Setup CardLayout untuk transisi antar halaman
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(Theme.PASTEL_BG);

        // 1. Buat Panel Menu Utama (Tombol-tombol)
        JPanel menuPanel = createMainMenuPanel();
        
        // 2. Buat Panel Leaderboard (File terpisah yang kita ubah jadi Panel)
        // Kita berikan aksi "Back" agar Leaderboard bisa kembali ke menu ini
        leaderboardPanel = new LeaderboardPanel(() -> showCard("MENU"));

        // Masukkan ke CardLayout
        mainContainer.add(menuPanel, "MENU");
        mainContainer.add(leaderboardPanel, "LEADERBOARD");

        add(mainContainer);
    }

    private void showCard(String cardName) {
        if ("LEADERBOARD".equals(cardName)) {
            leaderboardPanel.loadData(); // Refresh data saat membuka leaderboard
        }
        cardLayout.show(mainContainer, cardName);
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.PASTEL_BG);

        Theme.RoundedPanel menuCard = new Theme.RoundedPanel(40, Theme.PASTEL_CARD);
        menuCard.setLayout(new BoxLayout(menuCard, BoxLayout.Y_AXIS));
        menuCard.setBorder(new EmptyBorder(40, 60, 40, 60));
        menuCard.setPreferredSize(new Dimension(400, 500));

        JLabel titleLabel = new JLabel("Main Menu");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.PASTEL_TITLE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnPlay = createMenuButton("1. Main Game");
        JButton btnLeaderboard = createMenuButton("2. Leaderboard");
        JButton btnExit = createMenuButton("3. Keluar");
        JButton btnSwitch = createMenuButton("4. Ganti Akun");

        // --- Action Listeners ---
        
        // 1. Main Game
        btnPlay.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Game Panel akan segera hadir!");
        });

        // 2. Leaderboard (Sekarang ganti Card, bukan new Window)
        btnLeaderboard.addActionListener(e -> {
            showCard("LEADERBOARD");
        });

        // 3. Keluar
        btnExit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Keluar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        // 4. Ganti Akun
        btnSwitch.addActionListener(e -> {
            this.dispose();
            new GameLauncher().setVisible(true);
        });

        // Susun Layout
        menuCard.add(titleLabel);
        menuCard.add(Box.createRigidArea(new Dimension(0, 40)));
        menuCard.add(btnPlay);
        menuCard.add(Box.createRigidArea(new Dimension(0, 15)));
        menuCard.add(btnLeaderboard);
        menuCard.add(Box.createRigidArea(new Dimension(0, 15)));
        menuCard.add(btnExit);
        menuCard.add(Box.createRigidArea(new Dimension(0, 15)));
        menuCard.add(btnSwitch);

        panel.add(menuCard);
        return panel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(Theme.BTN_HOVER.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(Theme.BTN_HOVER);
                } else {
                    g2.setColor(Theme.BTN_MAIN);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                g2.setColor(Color.WHITE);
                g2.setFont(Theme.FONT_BUTTON);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setPreferredSize(new Dimension(300, 50));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}