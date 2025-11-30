import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LeaderboardPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private Runnable onBackAction;

    public LeaderboardPanel(Runnable onBackAction) {
        this.onBackAction = onBackAction;
        
        setLayout(new BorderLayout());
        setBackground(Theme.PASTEL_BG);
        setBorder(new EmptyBorder(40, 60, 40, 60)); // Margin halaman

        // --- Header ---
        JLabel titleLabel = new JLabel("Top Players");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.PASTEL_TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // --- Table ---
        String[] columnNames = {"Rank", "Username", "Score"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mencegah edit isi sel
            }
        };
        table = new JTable(model);
        styleTable(); // Panggil styling (termasuk fix kolom geser)

        // Bungkus tabel dengan Card Panel agar estetik
        Theme.RoundedPanel tableCard = new Theme.RoundedPanel(30, Color.WHITE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(20, 20, 20, 20)); 
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // --- Footer (Tombol Kembali) ---
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Theme.PASTEL_BG);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton btnBack = createBackButton("Kembali ke Menu");
        btnBack.addActionListener(e -> {
            if (this.onBackAction != null) {
                this.onBackAction.run();
            }
        });
        footerPanel.add(btnBack);

        // --- Susun Layout ---
        add(titleLabel, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        model.setRowCount(0); // Bersihkan data lama
        try {
            Connection conn = KoneksiDB.configDB();
            if (conn == null) return;

            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery("SELECT * FROM users ORDER BY score DESC LIMIT 10");
            
            int rank = 1;
            while (res.next()) {
                model.addRow(new Object[]{
                    rank++,
                    res.getString("username"),
                    res.getInt("score")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }
    }

    private void styleTable() {
        // --- PERBAIKAN DI SINI ---
        // Mematikan fitur geser kolom (Reordering)
        table.getTableHeader().setReorderingAllowed(false);
        
        // Mematikan fitur resize kolom (Opsional, jika ingin ukuran fix)
        // table.getTableHeader().setResizingAllowed(false); 

        table.setFont(Theme.FONT_REGULAR);
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Header Style
        table.getTableHeader().setFont(Theme.FONT_BUTTON);
        table.getTableHeader().setBackground(Theme.BTN_MAIN);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // Alignment Center untuk Rank dan Score
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Rank
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Score
    }

    private JButton createBackButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(Theme.PASTEL_TEXT.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(Theme.PASTEL_TEXT);
                } else {
                    g2.setColor(Theme.LINK_COLOR);
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
        button.setPreferredSize(new Dimension(200, 45));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}