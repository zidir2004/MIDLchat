package tp_tech;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private Connection conn;

    public UserDAO() {
        this.conn = DatabaseConnection.connect();
    }

    // Méthode pour ajouter un utilisateur dans la base de données
    public boolean registerUser(User user) {
        String query = "INSERT INTO utilisateurs (nom, email, mot_de_passe) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getMotDePasse());
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour vérifier si un utilisateur existe déjà avec le même email

 // Méthode pour vérifier si l'email existe
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM utilisateurs WHERE email = ?";
        try {
            // Vérifier que la connexion est valide avant d'exécuter la requête
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } else {
                System.err.println("❌ Connexion à la base de données est null.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Méthode pour vérifier si le mot de passe est correct
    public boolean checkPassword(String email, String motDePasse) {
        String query = "SELECT mot_de_passe FROM utilisateurs WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("mot_de_passe");
                return storedPassword.equals(motDePasse); // Vérifie si le mot de passe est correct
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du mot de passe : " + e.getMessage());
        }
        return false;
    }

}
