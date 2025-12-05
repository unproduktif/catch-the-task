import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KoneksiDB {

    private static final String DB_NAME = "catchthetask_db";
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/" + DB_NAME +
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static Connection connection;
    private static int loggedInUserId = -1;

    public static void setLoggedInUserId(int id) {
        loggedInUserId = id;
    }

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public static String getLoggedInUsername() {
        if (loggedInUserId == -1) return null;

        try (Connection conn = getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username FROM user WHERE user_id = ?"
            );

            ps.setInt(1, loggedInUserId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            }

        } catch (Exception e) {
            System.out.println("[GET USERNAME ERROR] " + e.getMessage());
        }

        return null;
    }

    public static void initialize() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("[DRIVER ERROR] " + e.getMessage());
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/?useSSL=false", USERNAME, PASSWORD)) {

            conn.createStatement().executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS " + DB_NAME);

        } catch (Exception e) {
            System.out.println("[CREATE DB ERROR] " + e.getMessage());
        }

        try (Connection conn = getConnection()) {

            conn.createStatement().executeUpdate("""
                CREATE TABLE IF NOT EXISTS user (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(50) BINARY NOT NULL
                )
            """);

            conn.createStatement().executeUpdate("""
                CREATE TABLE IF NOT EXISTS score (
                    score_id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    score INT NOT NULL,
                    time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES user(user_id)
                )
            """);

        } catch (Exception e) {
            System.out.println("[TABLE ERROR] " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            }
        } catch (Exception e) {
            System.out.println("[CONNECTION ERROR] " + e.getMessage());
        }
        return connection;
    }

    public static boolean registerUser(String username, String pass) {
        try (Connection conn = getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO user(username, password) VALUES(?, ?)");
            ps.setString(1, username);
            ps.setString(2, pass);
            ps.executeUpdate();

            return true;

        } catch (Exception e) {
            System.out.println("[REGISTER ERROR] " + e.getMessage());
            return false;
        }
    }

    public static int loginUser(String username, String pass) {
        try (Connection conn = getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id FROM user WHERE BINARY username=? AND BINARY password=?");

            ps.setString(1, username);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("user_id");

        } catch (Exception e) {
            System.out.println("[LOGIN ERROR] " + e.getMessage());
        }

        return -1;
    }

    public static boolean insertScore(int userId, int score) {
        try (Connection conn = getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO score(user_id, score) VALUES(?, ?)");

            ps.setInt(1, userId);
            ps.setInt(2, score);
            ps.executeUpdate();

            return true;

        } catch (Exception e) {
            System.out.println("[INSERT SCORE ERROR] " + e.getMessage());
            return false;
        }
    }

    public static int getHighestScore(int userId) {
        int maxScore = 0;
        String sql = "SELECT MAX(score) AS maxscore FROM score WHERE user_id = ?";

        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                maxScore = rs.getInt("maxscore");
            }

        } catch (Exception e) {
            System.out.println("ERROR load highest score: " + e.getMessage());
        }
        return maxScore;
    }

    public static List<Object[]> getLeaderboard() {

        List<Object[]> result = new ArrayList<>();

        try (Connection conn = getConnection()) {

            ResultSet rs = conn.createStatement().executeQuery("""
                SELECT username, score, time
                FROM score JOIN user ON score.user_id = user.user_id
                ORDER BY score DESC, time ASC
                LIMIT 10
            """);

            while (rs.next()) {
                result.add(new Object[]{
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getTimestamp("time")
                });
            }

        } catch (Exception e) {
            System.out.println("[LEADERBOARD ERROR] " + e.getMessage());
        }

        return result;
    }

    public static List<Object[]> getScoresByUser(int userId) {
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                SELECT score, time FROM score
                WHERE user_id = ?
                ORDER BY time DESC
            """);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("score"),
                        rs.getTimestamp("time")
                });
            }
        } catch (Exception e) {
            System.out.println("[USER SCORE ERROR] " + e.getMessage());
        }

        return list;
    }
}
