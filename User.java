

public class User {
    private int id;
    private String nom;
    private String email;
    private String motDePasse;

    public User(int id, String nom, String email, String motDePasse) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getEmail() { return email; }
}
