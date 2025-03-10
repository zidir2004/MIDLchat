package tp_tech;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost/midlchat"; // Remplace par ton URL de BD
    private static final String USER = "root"; // Remplace par ton utilisateur MySQL
    private static final String PASSWORD = "root"; // Ajoute ton mot de passe si nécessaire

    /**
     * Méthode pour établir une connexion à la base de données.
     * @return Connection objet si la connexion est réussie, sinon null.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Charger le driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir la connexion
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion réussie à la base de données !");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver JDBC introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données : " + e.getMessage());
        }
        return connection;
    }

    // Méthode principale pour tester la connexion
    public static void main(String[] args) {
        getConnection();
    }

    public static Connection connect() {
        Connection connection = null;
        try {
            // Charger le driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir la connexion
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion réussie à la base de données !");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver JDBC introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données : " + e.getMessage());
        }
        return connection;
    }

}


