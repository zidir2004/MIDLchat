

import java.sql.*;

public class UserDAO {
    private Connection conn;

    public UserDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public boolean registerUser(String nom, String email, String motDePasse) {
        String query = "INSERT INTO utilisateurs (nom, email, mot_de_passe) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, email);
            stmt.setString(3, motDePasse);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public User loginUser(String email, String motDePasse) {
        String query = "SELECT * FROM utilisateurs WHERE email = ? AND mot_de_passe = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, motDePasse);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("nom"), email, motDePasse);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getUserId(String email) {
        String query = "SELECT id FROM utilisateurs WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            return -1;
        }
        return -1;
    }
}
