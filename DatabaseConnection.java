import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/midlchat";
    private static final String USER = "root";  // Remplace par ton utilisateur MySQL
    private static final String PASSWORD = "";  // Mets ton mot de passe MySQL

    private static Connection connection = null;

    // Méthode pour obtenir une connexion
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Chargement du driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Établir la connexion
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion réussie à la base de données !");
            } catch (ClassNotFoundException e) {
                System.err.println("Erreur : Driver JDBC introuvable !");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Erreur : Impossible de se connecter à la base de données !");
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Méthode pour fermer la connexion proprement
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ Connexion fermée proprement.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion.");
                e.printStackTrace();
            }
        }
    }

    // Test de connexion
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        DatabaseConnection.closeConnection();
    }
}
