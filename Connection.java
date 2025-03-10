package tp_tech;



import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
    private static final String URL = "jdbc:mysql://localhost:3306/midlchat"; // Adresse de la base de données
    private static final String USER = "root"; // Nom d'utilisateur MySQL
    private static final String PASSWORD = ""; // Mot de passe MySQL (laisser vide si pas de mot de passe)

    /**
     * Méthode pour établir la connexion à la base de données.
     * @return Connection objet de connexion à la base de données.
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir la connexion
            conn = (Connection) DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion réussie à la base de données !");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver JDBC introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
        return conn;
    }
}

