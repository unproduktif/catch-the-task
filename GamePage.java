import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.sound.sampled.*;

import java.io.ByteArrayInputStream;
import java.io.File;

public class GamePage extends JPanel implements KeyListener {

    private Thread gameThread;
    private volatile boolean running = false;

    private Hero hero;
    private final ArrayList<Task> tasks = new ArrayList<>();
    private final Random rand = new Random();

    private Image bg;
    private Image uts, uas, laporan, project;
    private Image dl1, dl2;

    private Image heroRight, heroLeft, heroIdle;
    private Image heartFull, heartEmpty;


    private AudioFormat goodFormat;
    private byte[] goodData;
    private AudioFormat badFormat;
    private byte[] badData;

    private Clip bgm;


    private String username;
    private int score = 0;
    private int highestScore = 0;
    private int health = 3;
    private static final int MAX_HEALTH = 3;

    private boolean isPaused = false;
    private boolean isGameOver = false;
    private boolean gameOverShown = false;

    private boolean leftPressed, rightPressed, upPressed, downPressed;

    private JPanel gameOverPanel;
    private JLabel lblScoreGO;
    private JLabel lblBestGO;

    private final Runnable onHomeAction;

    private volatile boolean countdownRunning = true;
    private volatile int countdown = 3;

    public GamePage(Runnable onHomeAction) {
        this.onHomeAction = onHomeAction;

        setPreferredSize(new Dimension(900, 600));
        setLayout(null);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        loadAssets();
        startCountdown();
        highestScore = loadHighestScoreFromDB();
        username = KoneksiDB.getLoggedInUsername(); 
        if (username == null) username = "Player";

        hero = new Hero(
                420, 480, 70, 70, 8,
                heroRight, heroLeft, heroIdle,
                this
        );

        setupGameOverPanel();
        startCountdown();

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

        startGameLoop();                      
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void startGameLoop() {
        running = true;
        gameThread = new Thread(() -> {

            final double nsPerUpdate = 1_000_000_000.0 / 60.0; 
            long lastTime = System.nanoTime();
            double delta = 0;

            while (running) {
                long now = System.nanoTime();
                delta += (now - lastTime) / nsPerUpdate;
                lastTime = now;

                while (delta >= 1) {
                    updateGameLogic();
                    delta--;
                }

                SwingUtilities.invokeLater(this::repaint);

                try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }

        }, "GameLoopThread");

        gameThread.start();
    }

    public void stopTimer() {
        running = false;
        if (bgm != null) bgm.stop();
    }

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


        loadSFXBuffer("assets/sfx_good.wav", true);
        loadSFXBuffer("assets/sfx_bad.wav", false);
        bgm     = loadSound("assets/bgm_campus.wav");
    }

    private void loadSFXBuffer(String path, boolean isGood) {
        try {
            AudioInputStream ais;
            var url = getClass().getClassLoader().getResource(path);

            if (url != null)
                ais = AudioSystem.getAudioInputStream(url);
            else
                ais = AudioSystem.getAudioInputStream(new File(path));

            byte[] data = ais.readAllBytes();
            AudioFormat format = ais.getFormat();

            if (isGood) {
                goodData = data;
                goodFormat = format;
            } else {
                badData = data;
                badFormat = format;
            }

        } catch (Exception e) {
            System.out.println("LOAD SFX ERROR: " + e.getMessage());
        }
    }

    private void playGoodSFX() {
        if (goodData == null) return;

        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();

                AudioInputStream ais = new AudioInputStream(
                        new ByteArrayInputStream(goodData),
                        goodFormat,
                        goodData.length / goodFormat.getFrameSize()
                );

                clip.open(ais);
                clip.start();

            } catch (Exception e) {
                System.out.println("Play good SFX error: " + e.getMessage());
            }
        }).start();
    }

    private void playBadSFX() {
        if (badData == null) return;

        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();

                AudioInputStream ais = new AudioInputStream(
                        new ByteArrayInputStream(badData),
                        badFormat,
                        badData.length / badFormat.getFrameSize()
                );

                clip.open(ais);
                clip.start();

            } catch (Exception e) {
                System.out.println("Play bad SFX error: " + e.getMessage());
            }
        }).start();
    }

    private void playBGM() {
        if (bgm != null) {
            bgm.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private int loadHighestScoreFromDB() {
        int uid = KoneksiDB.getLoggedInUserId();
        if (uid == -1) return 0;
        return KoneksiDB.getHighestScore(uid); 
    }

    private void setupGameOverPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(10, 12, 28));
        card.setBorder(new LineBorder(Theme.PIXEL_BORDER_BLACK, 4));
        card.setBounds(260, 140, 380, 300);

        JLabel title = new JLabel("GAME OVER", SwingConstants.CENTER);
        title.setFont(Theme.PIXEL(34));
        title.setForeground(Theme.NEON_PINK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblScoreGO = new JLabel("SCORE: 0", SwingConstants.CENTER);
        lblScoreGO.setFont(Theme.PIXEL(22));
        lblScoreGO.setForeground(Color.WHITE);
        lblScoreGO.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblBestGO = new JLabel("BEST: 0", SwingConstants.CENTER);
        lblBestGO.setFont(Theme.PIXEL(20));
        lblBestGO.setForeground(Theme.NEON_YELLOW);
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
        b.setBackground(Theme.PIXEL_BUTTON_ORANGE);
        b.setBorder(new LineBorder(Theme.PIXEL_BORDER_BLACK, 3));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(260, 52));

        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setBackground(Theme.PIXEL_BUTTON_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(Theme.PIXEL_BUTTON_ORANGE);
            }
        });

        return b;
    }

    private void saveScoreAsync(int finalScore) {
        new Thread(() -> {
            int uid = KoneksiDB.getLoggedInUserId();
            if (uid != -1) {
                KoneksiDB.insertScore(uid, finalScore);
            }
        }, "SaveScoreThread").start();
    }

    private void restartGame() {
        score = 0;
        health = MAX_HEALTH;
        isPaused = true;
        gameOverShown = false;
        tasks.clear();

        hero.setX(420);
        hero.setY(480);

        gameOverPanel.setVisible(false);

        SwingUtilities.invokeLater(this::requestFocusInWindow);

        startCountdown();
    }


    private void startCountdown() {
        countdownRunning = true;
        countdown = 3;

        new Thread(() -> {
            try {
                while (countdown > 0) {
                    repaint();
                    Thread.sleep(1000);
                    countdown--;
                }

                repaint();
                Thread.sleep(900);

            } catch (Exception ignored) {}

            countdownRunning = false;
            isPaused = false;
            playBGM();
        }).start();
    }

    private void updateGameLogic() {
        if (countdownRunning) {
            return;
        }
        if (isPaused || isGameOver) {
            if (isGameOver && !gameOverShown) {
                gameOverShown = true;
                int finalScore = score;
                if (finalScore > highestScore) highestScore = finalScore;

                SwingUtilities.invokeLater(() -> {
                    lblScoreGO.setText("SCORE: " + finalScore);
                    lblBestGO.setText("BEST: " + highestScore);
                    gameOverPanel.setVisible(true);
                });

                saveScoreAsync(finalScore);
                if (bgm != null) bgm.stop();
            }
            return;
        }

        hero.doMovement();

        int panelW = getWidth() > 0 ? getWidth() : 900;
        if (rand.nextInt(25) == 0) {
            int x   = rand.nextInt(Math.max(1, panelW - 50));
            int spd = 3 + rand.nextInt(4);
            int t   = rand.nextInt(6);

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

                if (score >= highestScore) {
                    highestScore = score;
                }

                if (t instanceof Bad) {
                    health--;
                    playBadSFX();

                } else {
                    playGoodSFX();
                }

                it.remove();
            }
        }

        if (health <= 0 && !isGameOver) {
            isGameOver = true;
        }
    }

 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (bg != null) {
            g2.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setColor(Theme.PIXEL_BG_PANEL);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        ArrayList<Task> renderList = new ArrayList<>(tasks);
        for (Task t : renderList) {
            t.Draw(g2);
        }

        hero.Draw(g2);

        drawHUD(g2);
        if (countdownRunning) {
            g2.setFont(Theme.PIXEL(80));

            g2.setColor(Color.BLACK);
            String text = (countdown > 0) ? String.valueOf(countdown) : "GO!";
            int tw = g2.getFontMetrics().stringWidth(text);
            int x = (getWidth() - tw) / 2;
            int y = getHeight() / 2;

            g2.drawString(text, x - 3, y);
            g2.drawString(text, x + 3, y);
            g2.drawString(text, x, y - 3);
            g2.drawString(text, x, y + 3);

            g2.setColor(new Color(0, 255, 255));
            g2.drawString(text, x, y);
        }

        g2.setFont(Theme.PIXEL(20));
        g2.setColor(Color.WHITE);

        String userText = "User: " + username;
        int textWidth = g2.getFontMetrics().stringWidth(userText);

        g2.drawString(userText, getWidth() - textWidth - 20, 40);
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(10, 10, 270, 80, 16, 16);

        g2.setFont(Theme.PIXEL(22));
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score, 20, 40);

        g2.setColor(Theme.NEON_YELLOW);
        g2.drawString("Best : " + highestScore, 20, 70);

        int hx = 300;
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
        if (c == KeyEvent.VK_UP    || c == KeyEvent.VK_W) return;
        if (c == KeyEvent.VK_DOWN  || c == KeyEvent.VK_S) return;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int c = e.getKeyCode();

        if (c == KeyEvent.VK_LEFT  || c == KeyEvent.VK_A) leftPressed  = false;
        if (c == KeyEvent.VK_RIGHT || c == KeyEvent.VK_D) rightPressed = false;
        if (c == KeyEvent.VK_UP    || c == KeyEvent.VK_W) return;
        if (c == KeyEvent.VK_DOWN  || c == KeyEvent.VK_S) return;
    }

    @Override public void keyTyped(KeyEvent e) {}

    public boolean isLeftPressed()  { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isUpPressed()    { return upPressed; }
    public boolean isDownPressed()  { return downPressed; }
}
