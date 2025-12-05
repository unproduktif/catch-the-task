import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public LoginPage() {

        setTitle("Catch The Task – Neon Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        JLabel bg = new JLabel(new ImageIcon("assets/bg_campus.png"));
        setContentPane(bg);
        setLayout(new GridBagLayout());

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createRegisterPanel(), "REGISTER");

        add(mainPanel);
    }

    private JPanel createLoginPanel() {
        JPanel card = createPixelCard();

        JLabel banner = createBanner("LOGIN");

        JTextField userField = createPixelField();
        JPasswordField passField = createPixelPassword();

        JButton loginBtn = createPixelButton("START GAME");
        JLabel goRegister = createPixelLink("CREATE NEW ACCOUNT", "REGISTER");

        loginBtn.addActionListener(e -> login(userField, passField));

        card.add(banner);
        addGroup(card, "USERNAME", userField);
        addGroup(card, "PASSWORD", passField);
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(18));
        card.add(goRegister);

        return wrap(card);
    }

    private JPanel createRegisterPanel() {
        JPanel card = createPixelCard();

        JLabel banner = createBanner("REGISTER");

        JTextField userField = createPixelField();
        JPasswordField passField = createPixelPassword();

        JButton regBtn = createPixelButton("CREATE");
        JLabel backLogin = createPixelLink("BACK TO LOGIN", "LOGIN");

        regBtn.addActionListener(e -> register(userField, passField));

        card.add(banner);
        addGroup(card, "NEW USERNAME", userField);
        addGroup(card, "NEW PASSWORD", passField);
        card.add(regBtn);
        card.add(Box.createVerticalStrut(18));
        card.add(backLogin);

        return wrap(card);
    }

    private void login(JTextField userField, JPasswordField passField) {

        String username = userField.getText();
        String password = new String(passField.getPassword());

        int userId = KoneksiDB.loginUser(username, password);

        if (userId != -1) {

            KoneksiDB.setLoggedInUserId(userId);

            JOptionPane.showMessageDialog(this,
                    "WELCOME, " + username + "!",
                    "SUCCESS",
                    JOptionPane.INFORMATION_MESSAGE);

            new HomePage().setVisible(true);
            dispose();

        } else {
            JOptionPane.showMessageDialog(this,
                    "WRONG USERNAME OR PASSWORD!",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void register(JTextField userField, JPasswordField passField) {

        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "FIELDS CANNOT BE EMPTY!");
            return;
        }

        if (KoneksiDB.registerUser(username, password)) {
            JOptionPane.showMessageDialog(this, "ACCOUNT CREATED!");
            cardLayout.show(mainPanel, "LOGIN");
        } else {
            JOptionPane.showMessageDialog(this,
                    "USERNAME ALREADY EXISTS!",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createPixelCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);

        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK, 6),
                new LineBorder(Color.DARK_GRAY, 6)
        ));

        card.setBackground(new Color(10, 15, 30));
        card.setPreferredSize(new Dimension(420, 500));
        return card;
    }

    private JPanel wrap(JPanel panel) {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        wrap.add(panel);
        return wrap;
    }

    private JLabel createBanner(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.PIXEL(36));
        lbl.setForeground(Theme.NEON_CYAN);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(20, 25, 50));
        lbl.setBorder(new LineBorder(Color.BLACK, 6));
        lbl.setMaximumSize(new Dimension(260, 80));
        return lbl;
    }

    private JTextField createPixelField() {
        JTextField f = new JTextField();
        f.setFont(Theme.PIXEL(20));
        f.setBackground(new Color(15, 20, 40));
        f.setForeground(Color.WHITE);
        f.setBorder(new LineBorder(Color.BLACK, 4));
        f.setMaximumSize(new Dimension(280, 50));
        return f;
    }

    private JPasswordField createPixelPassword() {
        JPasswordField f = new JPasswordField();
        f.setFont(Theme.PIXEL(20));
        f.setBackground(new Color(15, 20, 40));
        f.setForeground(Color.WHITE);
        f.setBorder(new LineBorder(Color.BLACK, 4));
        f.setMaximumSize(new Dimension(280, 50));
        return f;
    }

    private void addGroup(JPanel parent, String labelText, JComponent field) {
        JLabel l = new JLabel(labelText);
        l.setFont(Theme.PIXEL(20));
        l.setForeground(Color.WHITE);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);

        parent.add(Box.createVerticalStrut(25));
        parent.add(l);
        parent.add(Box.createVerticalStrut(5));
        parent.add(field);
    }

    private JButton createPixelButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(Theme.PIXEL(24));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(255, 136, 0));
        btn.setBorder(new LineBorder(Color.BLACK, 6));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(260, 60));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        return btn;
    }

    private JLabel createPixelLink(String text, String targetCard) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.PIXEL(16));
        l.setForeground(Color.WHITE);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        l.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, targetCard);
            }
        });

        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }
}
