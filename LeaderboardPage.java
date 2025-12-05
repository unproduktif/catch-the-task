import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LeaderboardPage extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private Runnable onBackAction;

    // Tema final tanpa biru & tanpa abu-abu
    private final Color PANEL_BG = new Color(0, 0, 0);
    private final Color BORDER = new Color(50, 0, 64);
    private final Color HEADER_BG = new Color(150, 0, 200);
    private final Color HEADER_TXT = Color.WHITE;
    private final Color ROW_1 = new Color(38, 0, 52);
    private final Color ROW_2 = new Color(58, 0, 78);
    private final Color TEXT_COLOR = Color.WHITE;

    public LeaderboardPage(Runnable onBackAction) {

        this.onBackAction = onBackAction;

        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(760, 540));
        panel.setBackground(PANEL_BG);
        panel.setBorder(new LineBorder(BORDER, 6));

        JLabel title = new JLabel("LEADERBOARD", SwingConstants.CENTER);
        title.setFont(Theme.PIXEL(36));
        title.setForeground(Theme.NEON_CYAN);
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"#", "USERNAME", "SCORE", "DATE", "TIME"};

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(PANEL_BG);
        scroll.setBorder(new LineBorder(BORDER, 3));

        panel.add(scroll, BorderLayout.CENTER);

        JButton back = createButton("BACK");
        back.addActionListener(e -> {
            if (onBackAction != null) onBackAction.run();
        });

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(back);

        panel.add(bottom, BorderLayout.SOUTH);

        add(panel);
    }

    // ===========================================================
    // LOAD TOP 10
    // ===========================================================
    public void loadData() {
        model.setRowCount(0);

        List<Object[]> list = KoneksiDB.getLeaderboard();
        int rank = 1;

        SimpleDateFormat dfDate = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");

        for (Object[] r : list) {
            if (rank > 10) break;

            String username = (String) r[0];
            int score       = Math.max((int) r[1], 0);
            Date timestamp  = (Date) r[2];

            model.addRow(new Object[]{
                    rank++,
                    username,
                    score,
                    dfDate.format(timestamp),
                    dfTime.format(timestamp)
            });
        }
    }

    // ===========================================================
    // TABLE STYLE
    // ===========================================================
    private void styleTable() {

        table.setFont(Theme.PIXEL(16));
        table.setRowHeight(36);
        table.setForeground(TEXT_COLOR);
        table.setShowGrid(false);
        table.setFocusable(false);

        // LOCK HEADER (no drag, no resize)
        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.PIXEL(18));
        header.setBackground(HEADER_BG);
        header.setForeground(HEADER_TXT);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Row renderer (tanpa highlight juara / user)
        DefaultTableCellRenderer cell = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean sel,
                    boolean focus, int row, int col) {

                Component c = super.getTableCellRendererComponent(
                        tbl, value, sel, focus, row, col
                );

                c.setFont(Theme.PIXEL(16));
                c.setForeground(TEXT_COLOR);

                // semua baris sama, hanya alternating ungu 2 tone
                c.setBackground(row % 2 == 0 ? ROW_1 : ROW_2);

                if (col == 0 || col >= 2)
                    setHorizontalAlignment(JLabel.CENTER);
                else
                    setHorizontalAlignment(JLabel.LEFT);

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(cell);
    }

    // ===========================================================
    // BUTTON
    // ===========================================================
    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFont(Theme.PIXEL(22));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(255, 140, 40));
        b.setBorder(new LineBorder(BORDER, 3));
        b.setPreferredSize(new Dimension(180, 52));
        b.setFocusPainted(false);

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(255, 170, 70));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(255, 140, 40));
            }
        });

        return b;
    }
}
