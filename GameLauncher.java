import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GameLauncher extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public GameLauncher() {
        setTitle("Catch The Task - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setMinimumSize(new Dimension(800, 600));

        getContentPane().setBackground(Theme.PASTEL_BG);
        setLayout(new GridBagLayout()); 

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false); 

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createRegisterPanel(), "REGISTER");

        add(mainPanel);
    }

    private JPanel createCardPanel() {
        Theme.RoundedPanel card = new Theme.RoundedPanel(40, Theme.PASTEL_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(50, 50, 50, 50)); 
        card.setPreferredSize(new Dimension(400, 550)); 
        return card;
    }

    // --- PANEL LOGIN ---
    private JPanel createLoginPanel() {
        JPanel panel = createCardPanel();

        JLabel title = new JLabel("Welcome Back!");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.PASTEL_TITLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = createStyledTextField();
        JPasswordField passField = createStyledPasswordField();
        
        JButton loginBtn = createRoundedButton("Login");
        JLabel toggleLabel = createClickableLabel("Belum punya akun? Daftar", "REGISTER");

        // --- LOGIKA LOGIN ---
        ActionListener loginAction = e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            
            try {
                Connection conn = KoneksiDB.configDB();
                if (conn == null) return; 

                // Menggunakan BINARY agar Case Sensitive
                String sql = "SELECT * FROM users WHERE BINARY username = ? AND BINARY password = ?";
                
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, user);
                pst.setString(2, pass);
                ResultSet rs = pst.executeQuery();
                
                if(rs.next()){
                    JOptionPane.showMessageDialog(this, "Login Berhasil!");
                    new MenuPage().setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        };

        // 1. Pasang aksi ke Tombol Login
        loginBtn.addActionListener(loginAction);

        // 2. [BARU] Pasang aksi "Enter" ke Text Field
        // Jadi kalau tekan Enter di kolom username/password, sama saja dengan klik tombol login
        userField.addActionListener(loginAction);
        passField.addActionListener(loginAction);

        // --- Menyusun Layout ---
        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        addInputGroup(panel, "Username", userField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        addInputGroup(panel, "Password", passField);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(loginBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(toggleLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // --- PANEL REGISTER ---
    private JPanel createRegisterPanel() {
        JPanel panel = createCardPanel();

        JLabel title = new JLabel("Create Account");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.PASTEL_TITLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = createStyledTextField();
        JPasswordField passField = createStyledPasswordField();
        
        JButton regBtn = createRoundedButton("Register");
        JLabel toggleLabel = createClickableLabel("Sudah punya akun? Login", "LOGIN");

        // --- LOGIKA REGISTER ---
        ActionListener registerAction = e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if(user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data tidak boleh kosong!");
                return;
            }

            try {
                String sql = "INSERT INTO users (username, password, score) VALUES (?, ?, 0)";
                Connection conn = KoneksiDB.configDB();
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, user);
                pst.setString(2, pass);
                pst.execute();
                
                JOptionPane.showMessageDialog(this, "Registrasi Berhasil! Silakan Login.");
                userField.setText(""); passField.setText("");
                cardLayout.show(mainPanel, "LOGIN");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Username sudah digunakan, coba yang lain.");
            }
        };

        // 1. Pasang aksi ke Tombol Register
        regBtn.addActionListener(registerAction);

        // 2. [BARU] Pasang aksi "Enter" juga untuk Register
        userField.addActionListener(registerAction);
        passField.addActionListener(registerAction);

        // --- Menyusun Layout ---
        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        addInputGroup(panel, "Username", userField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        addInputGroup(panel, "Password", passField);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(regBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(toggleLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // --- Helper Components (Tidak Berubah) ---
    private void addInputGroup(JPanel parentPanel, String labelText, JComponent field) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.FONT_REGULAR);
        label.setForeground(Theme.PASTEL_TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height + 30));
        
        group.add(label);
        group.add(Box.createRigidArea(new Dimension(0, 8)));
        group.add(field);
        parentPanel.add(group);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220,220,220)); 
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        styleField(field);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220,220,220));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        styleField(field);
        return field;
    }

    private void styleField(JTextField field) {
        field.setBackground(Color.WHITE);
        field.setForeground(Color.GRAY);
        field.setCaretColor(Theme.PASTEL_TITLE);
        field.setOpaque(false);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); 
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setBorder(new EmptyBorder(10, 15, 10, 15)); 
        field.setFont(Theme.FONT_REGULAR);
    }

    private JButton createRoundedButton(String text) {
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(Theme.FONT_BUTTON);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel createClickableLabel(String text, String targetCard) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Theme.PASTEL_TEXT);
        label.setFont(Theme.FONT_REGULAR);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, targetCard);
            }
            public void mouseEntered(MouseEvent e) {
                label.setForeground(Theme.LINK_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                label.setForeground(Theme.PASTEL_TEXT);
            }
        });
        return label;
    }

    public static void main(String[] args) {
        KoneksiDB.prepareDatabase(); 
        
        SwingUtilities.invokeLater(() -> {
            new GameLauncher().setVisible(true);
        });
    }
}