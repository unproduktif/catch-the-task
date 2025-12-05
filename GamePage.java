import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;

public class GamePage extends JPanel implements ActionListener, KeyListener {

    // Core
    private Timer timer;
    private Hero hero;
    private final ArrayList<Task> tasks = new ArrayList<>();
    private final Random rand = new Random();

    // Assets
    private Image bg;
    private Image uts, uas, laporan, project;
    private Image dl1, dl2;

    private Image heroRight, heroLeft, heroIdle;

    private Image heartFull, heartEmpty;

    private Clip sfxGood, sfxBad, bgm;

    // Game state
    private int score = 0;
    private int highestScore = 0;
    private int health = 3;
    private static final int MAX_HEALTH = 3;

    private boolean isPaused = false;
    private boolean isGameOver = false;

    // Input flags
    private boolean leftPressed, rightPressed, upPressed, downPressed;

    // Game Over UI
    private JPanel gameOverPanel;
    private JLabel lblScoreGO;
    private JLabel lblBestGO;

    private final Runnable onHomeAction;

    public GamePage(Runnable onHomeAction) {
        this.onHomeAction = onHomeAction;

        setPreferredSize(new Dimension(900, 600));
        setLayout(null); // pakai absolute untuk panel game over
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        loadAssets();
        playBGM();

        // Hero dengan 3 sprite
        hero = new Hero(
                420, 480, 70, 70, 8,
                heroRight, heroLeft, heroIdle,
                this
        );

        setupGameOverPanel();

        // Mouse movement horizontal
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!isPaused && !isGameOver && hero != null) {
                    int nx = e.getX() - hero.getWidth() / 2;
                    int panelW = getWidth() > 0 ? getWidth() : 900;
                    if (nx < 0) nx = 0;
                    if (nx > panelW - hero.getWidth()) nx = panelW - hero.getWidth();
                    hero.setX(nx);
                }
            }
        });

        timer = new Timer(16, this);
        timer.start();

        // Pastikan panel punya fokus untuk keyboard
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    // =========================================================
    // ASSETS
    // =========================================================
    private Image loadImage(String path) {
        try {
            var url = getClass().getClassLoader().getResource(path);
            return (url != null)
                    ? new ImageIcon(url).getImage()
                    : new ImageIcon(path).getImage();
        } catch (Exception e) {
            System.out.println("[IMG ERROR] " + path + " -> " + e.getMessage());
            return null;
        }
    }

    private Clip loadSound(String path) {
        try {
            var url = getClass().getClassLoader().getResource(path);
            Clip clip = AudioSystem.getClip();
            if (url != null) {
                clip.open(AudioSystem.getAudioInputStream(url));
            } else {
                clip.open(AudioSystem.getAudioInputStream(new File(path)));
            }
            return clip;
        } catch (Exception e) {
            System.out.println("[SOUND ERROR] " + path + " -> " + e.getMessage());
            return null;
        }
    }

    private void loadAssets() {
        bg = loadImage("assets/bg_kampus.png");

        uts     = loadImage("assets/tugas_uts.png");
        uas     = loadImage("assets/tugas_uas.png");
        laporan = loadImage("assets/tugas_laporan.png");
        project = loadImage("assets/tugas_project.png");
        dl1     = loadImage("assets/deadline_alarm.png");
        dl2     = loadImage("assets/deadline_red.png");

        heroRight = loadImage("assets/player_kanan.png");
        heroLeft  = loadImage("assets/player_kiri.png");
        heroIdle  = loadImage("assets/player_diam.png");

        heartFull  = loadImage("assets/heart.png");
        heartEmpty = loadImage("assets/heart_empty.png");

        sfxGood = loadSound("assets/sfx_good.wav");
        sfxBad  = loadSound("assets/sfx_bad.wav");
        bgm     = loadSound("assets/bgm_campus.wav");
    }

    private void playBGM() {
        if (bgm != null) {
            bgm.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void playClip(Clip c) {
        if (c == null) return;
        c.stop();
        c.setFramePosition(0);
        c.start();
    }

    public void stopTimer() {
        if (timer != null) timer.stop();
        if (bgm != null) bgm.stop();
    }

    // =========================================================
    // GAME OVER UI
    // =========================================================
    private void setupGameOverPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(15, 15, 30));
        card.setBorder(new LineBorder(Color.WHITE, 3, true));
        card.setBounds(260, 140, 380, 300);

        JLabel title = new JLabel("GAME OVER", SwingConstants.CENTER);
        title.setFont(Theme.PIXEL(36));
        title.setForeground(new Color(255, 80, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblScoreGO = new JLabel("SCORE: 0", SwingConstants.CENTER);
        lblScoreGO.setFont(Theme.PIXEL(22));
        lblScoreGO.setForeground(Color.WHITE);
        lblScoreGO.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblBestGO = new JLabel("BEST: 0", SwingConstants.CENTER);
        lblBestGO.setFont(Theme.PIXEL(20));
        lblBestGO.setForeground(new Color(255, 215, 0));
        lblBestGO.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton retry = createButton("PLAY AGAIN");
        JButton home  = createButton("BACK TO MENU");

        retry.addActionListener(e -> restartGame());
        home.addActionListener(e -> {
            stopTimer();
            if (onHomeAction != null) onHomeAction.run();
        });

        card.add(Box.createVerticalStrut(20));
        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(lblScoreGO);
        card.add(Box.createVerticalStrut(5));
        card.add(lblBestGO);
        card.add(Box.createVerticalStrut(20));
        card.add(retry);
        card.add(Box.createVerticalStrut(10));
        card.add(home);

        gameOverPanel = card;
        gameOverPanel.setVisible(false);
        add(gameOverPanel);
    }

    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFont(Theme.PIXEL(20));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(0, 140, 255));
        b.setBorder(new LineBorder(Color.BLACK, 2));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(240, 50));
        return b;
    }

    // =========================================================
    // SCORE
    // =========================================================
    private void saveScore() {
        int uid = KoneksiDB.getLoggedInUserId();
        if (uid == -1) return;
        KoneksiDB.insertScore(uid, score);
    }

    private void restartGame() {
        score = 0;
        health = MAX_HEALTH;
        isPaused = false;
        isGameOver = false;
        tasks.clear();

        hero.setX(420);
        hero.setY(480);

        gameOverPanel.setVisible(false);
        playBGM();

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    // =========================================================
    // GAME LOOP
    // =========================================================
    @Override
    public void actionPerformed(ActionEvent e) {

        if (isPaused || isGameOver) {
            if (isGameOver && !gameOverPanel.isVisible()) {
                lblScoreGO.setText("SCORE: " + score);
                if (score > highestScore) highestScore = score;
                lblBestGO.setText("BEST: " + highestScore);

                gameOverPanel.setVisible(true);
                saveScore();
            }
            repaint();
            return;
        }

        // Update hero
        hero.doMovement();

        // Spawn task
        int panelW = getWidth() > 0 ? getWidth() : 900;
        if (rand.nextInt(25) == 0) {
            int x = rand.nextInt(Math.max(1, panelW - 50));
            int spd = 3 + rand.nextInt(4);
            int t = rand.nextInt(6);

            Task task = switch (t) {
                case 0 -> new Good(x, 0, 50, 50, spd, 10, uts);
                case 1 -> new Good(x, 0, 50, 50, spd, 10, uas);
                case 2 -> new Good(x, 0, 50, 50, spd, 15, laporan);
                case 3 -> new Good(x, 0, 50, 50, spd, 20, project);
                case 4 -> new Bad(x, 0, 50, 50, spd, -10, dl1);
                default -> new Bad(x, 0, 50, 50, spd, -15, dl2);
            };

            tasks.add(task);
        }

        // Update tasks & collision
        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            t.doMovement();

            if (t.getY() > getHeight()) {
                it.remove();
                continue;
            }

            if (hero.isCatch(t)) {
                score += t.getPoin();

                if (t instanceof Bad) {
                    health--;
                    playClip(sfxBad);
                } else {
                    playClip(sfxGood);
                }

                it.remove();
            }
        }

        if (health <= 0 && !isGameOver) {
            isGameOver = true;
            if (bgm != null) bgm.stop();
        }

        repaint();
    }

    // =========================================================
    // RENDER
    // =========================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Background
        if (bg != null) {
            g2.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setColor(new Color(10, 15, 25));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // Tasks
        for (Task t : tasks) {
            t.Draw(g2);
        }

        // Hero
        hero.Draw(g2);

        // HUD
        drawHUD(g2);
    }

    private void drawHUD(Graphics2D g2) {
        // Panel translucent untuk HUD
        g2.setColor(new Color(0, 0, 0, 130));
        g2.fillRoundRect(10, 10, 260, 80, 16, 16);

        g2.setFont(Theme.PIXEL(22));
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score, 20, 40);

        g2.setColor(Color.YELLOW);
        g2.drawString("Best : " + highestScore, 20, 70);

        // HP hearts
        int hx = 290;
        int hy = 20;
        for (int i = 0; i < MAX_HEALTH; i++) {
            Image img = (i < health) ? heartFull : heartEmpty;
            if (img != null) {
                g2.drawImage(img, hx, hy, 32, 32, null);
            } else {
                g2.setColor(i < health ? Color.RED : Color.GRAY);
                g2.fillOval(hx, hy, 24, 24);
            }
            hx += 36;
        }
    }

    // =========================================================
    // INPUT
    // =========================================================
    @Override
    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();

        if (c == KeyEvent.VK_ESCAPE) {
            if (!isGameOver) {
                isPaused = !isPaused;
            }
            return;
        }

        if (isPaused || isGameOver) return;

        if (c == KeyEvent.VK_LEFT  || c == KeyEvent.VK_A) leftPressed  = true;
        if (c == KeyEvent.VK_RIGHT || c == KeyEvent.VK_D) rightPressed = true;
        if (c == KeyEvent.VK_UP    || c == KeyEvent.VK_W) upPressed    = true;
        if (c == KeyEvent.VK_DOWN  || c == KeyEvent.VK_S) downPressed  = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int c = e.getKeyCode();

        if (c == KeyEvent.VK_LEFT  || c == KeyEvent.VK_A) leftPressed  = false;
        if (c == KeyEvent.VK_RIGHT || c == KeyEvent.VK_D) rightPressed = false;
        if (c == KeyEvent.VK_UP    || c == KeyEvent.VK_W) upPressed    = false;
        if (c == KeyEvent.VK_DOWN  || c == KeyEvent.VK_S) downPressed  = false;
    }

    @Override public void keyTyped(KeyEvent e) {}

    // Dipakai Hero
    public boolean isLeftPressed()  { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isUpPressed()    { return upPressed; }
    public boolean isDownPressed()  { return downPressed; }
}
