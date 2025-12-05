import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class HomePage extends JFrame {

    public static final String CARD_MENU        = "MENU";
    public static final String CARD_GAME        = "GAME";
    public static final String CARD_LEADERBOARD = "LEADERBOARD";

    private CardLayout cardLayout;
    private JPanel mainContainer;

    private GamePage gamePage;
    private LeaderboardPage leaderboardPage;

    public HomePage() {

        setTitle("Catch The Task - Neon");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        JPanel menuPanel = createMenuPanel();
        leaderboardPage = new LeaderboardPage(() -> showCard(CARD_MENU));

        mainContainer.add(menuPanel, CARD_MENU);
        mainContainer.add(leaderboardPage, CARD_LEADERBOARD);

        setContentPane(mainContainer);
        showCard(CARD_MENU);
    }

    public void showCard(String name) {

        if (CARD_GAME.equals(name)) {
            if (gamePage != null) {
                gamePage.stopTimer();
                mainContainer.remove(gamePage);
            }
            gamePage = new GamePage(this::showMenuCard);
            mainContainer.add(gamePage, CARD_GAME);
            gamePage.requestFocusInWindow();
        }

        if (CARD_LEADERBOARD.equals(name)) {
            leaderboardPage.loadData();
        }

        cardLayout.show(mainContainer, name);
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    private void showMenuCard() {
        showCard(CARD_MENU);
    }

    private JPanel createMenuPanel() {

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(true);
        wrap.setBackground(Theme.PIXEL_BG_PANEL);

        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 500));
        card.setBackground(new Color(5, 8, 20));

        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.PIXEL_SHADOW_DARK, 8),
                new LineBorder(Theme.PIXEL_BORDER_BLACK, 6)
        ));

        JLabel title = new JLabel("MAIN MENU", SwingConstants.CENTER);
        title.setFont(Theme.PIXEL(36));
        title.setForeground(Theme.NEON_CYAN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnPlay        = createButton("PLAY GAME");
        JButton btnLeaderboard = createButton("LEADERBOARD");
        JButton btnExit        = createButton("EXIT");
        JButton btnSwitch      = createButton("CHANGE ACCOUNT");

        btnPlay.addActionListener(e -> showCard(CARD_GAME));
        btnLeaderboard.addActionListener(e -> showCard(CARD_LEADERBOARD));

        btnExit.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(
                    this,
                    "Exit game?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );
            if (opt == JOptionPane.YES_OPTION) System.exit(0);
        });

        btnSwitch.addActionListener(e -> {
            KoneksiDB.setLoggedInUserId(-1);
            dispose();
            new LoginPage().setVisible(true);
        });

        card.add(Box.createVerticalStrut(25));
        card.add(title);
        card.add(Box.createVerticalStrut(40));
        card.add(btnPlay);
        card.add(Box.createVerticalStrut(20));
        card.add(btnLeaderboard);
        card.add(Box.createVerticalStrut(20));
        card.add(btnExit);
        card.add(Box.createVerticalStrut(20));
        card.add(btnSwitch);

        wrap.add(card);
        return wrap;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.PIXEL(22));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Theme.PIXEL_BUTTON_ORANGE);
        btn.setBorder(new LineBorder(Theme.PIXEL_BORDER_BLACK, 6));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(280, 60));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(Theme.PIXEL_BUTTON_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Theme.PIXEL_BUTTON_ORANGE);
            }
        });

        return btn;
    }
}
