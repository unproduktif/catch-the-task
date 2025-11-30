import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class KoneksiDB {
    private static Connection mysqlconfig;
    
    // Nama Database
    private static final String DB_NAME = "catchthetask_db";
    private static final String DB_USER = "root"; 
    private static final String DB_PASS = "";     

    public static Connection configDB() throws SQLException {
        try {
            if (mysqlconfig == null || mysqlconfig.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/" + DB_NAME;
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                mysqlconfig = DriverManager.getConnection(url, DB_USER, DB_PASS);
            }
        } catch (Exception e) {
            System.err.println("Koneksi gagal: " + e.getMessage());
        }
        return mysqlconfig;
    }

    public static void prepareDatabase() {
        Connection conn = null;
        Statement stmt = null;
        try {
            String rawUrl = "jdbc:mysql://localhost:3306/";
            conn = DriverManager.getConnection(rawUrl, DB_USER, DB_PASS);
            stmt = conn.createStatement();

            // 1. Buat DB
            String sqlCreateDB = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(sqlCreateDB);
            
            stmt.close(); conn.close();
            
            // 2. Konek ke DB Baru
            conn = DriverManager.getConnection(rawUrl + DB_NAME, DB_USER, DB_PASS);
            stmt = conn.createStatement();

            // 3. Buat Tabel (UPDATE: Tambah BINARY agar Case Sensitive di Database-level)
            String sqlCreateTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " + // Username biasanya case-insensitive untuk register
                    "password VARCHAR(50) BINARY NOT NULL, " + // Password WAJIB case-sensitive
                    "score INT DEFAULT 0" +
                    ")";
            stmt.executeUpdate(sqlCreateTable);
            System.out.println("Database & Table checked successfully.");

            // 4. Data Dummy
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO users (username, password, score) VALUES " +
                        "('Admin', 'admin123', 1000), " +
                        "('Player1', '123', 500)");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Menyiapkan Database: " + e.getMessage());
            System.exit(0);
        } finally {
            try { if (stmt != null) stmt.close(); if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}