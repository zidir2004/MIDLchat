package tp_tech;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Inscription {

    private JFrame frame;
    private JTextField tfNom;
    private JTextField tfEmail;
    private JPasswordField pfMotDePasse;
    private JButton btnInscrire;

    public Inscription() {
        // Créer le cadre de la fenêtre
        frame = new JFrame("Inscription");
        frame.setBounds(400, 200, 450, 350); // Taille de la fenêtre agrandie
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Définir un layout de type GridBagLayout pour plus de flexibilité
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espacement entre les composants

        // Changer la couleur de fond en rose
        frame.getContentPane().setBackground(new Color(255, 182, 193));

        // Ajouter des labels
        JLabel lblNom = new JLabel("Nom : ");
        JLabel lblEmail = new JLabel("Email : ");
        JLabel lblMotDePasse = new JLabel("Mot de Passe : ");
        
        // Personnaliser les labels pour une meilleure lisibilité
        lblNom.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMotDePasse.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Créer les champs de texte
        tfNom = new JTextField(20);
        tfEmail = new JTextField(20);
        pfMotDePasse = new JPasswordField(20);
        
        // Personnaliser les champs de texte
        tfNom.setFont(new Font("Arial", Font.PLAIN, 14));
        tfEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        pfMotDePasse.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Créer un bouton d'inscription
        btnInscrire = new JButton("S'inscrire");
        btnInscrire.setBackground(new Color(255, 105, 180)); // Couleur rose clair
        btnInscrire.setFont(new Font("Arial", Font.BOLD, 14));
        btnInscrire.setForeground(Color.WHITE);
        btnInscrire.setPreferredSize(new Dimension(150, 40));
        
        // Personnaliser l'apparence du bouton
        btnInscrire.setFocusPainted(false);
        btnInscrire.setBorder(BorderFactory.createRaisedBevelBorder());

        // Ajouter les composants à la fenêtre avec un layout flexible
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(lblNom, gbc);

        gbc.gridx = 1;
        frame.add(tfNom, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(lblEmail, gbc);

        gbc.gridx = 1;
        frame.add(tfEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(lblMotDePasse, gbc);

        gbc.gridx = 1;
        frame.add(pfMotDePasse, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        frame.add(btnInscrire, gbc);

        // Ajouter un ActionListener pour le bouton
        btnInscrire.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inscrireUtilisateur();
            }
        });

        // Rendre la fenêtre visible
        frame.setVisible(true);
    }

    private void inscrireUtilisateur() {
        String nom = tfNom.getText();
        String email = tfEmail.getText();
        String motDePasse = new String(pfMotDePasse.getPassword());

        // Vérification si les champs sont vides
        if (nom.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Tous les champs doivent être remplis", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

     // Vérification du format de l'email
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(frame, "L'email n'est pas valide", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérification de la validité du mot de passe
        if (!isValidPassword(motDePasse)) {
            JOptionPane.showMessageDialog(frame, "Le mot de passe doit contenir au moins 5 caractères", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Vérifier si l'email est déjà utilisé
        UserDAO userDAO = new UserDAO();
        if (userDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(frame, "Cet email est déjà utilisé", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Créer un utilisateur
        User user = new User(nom, email, motDePasse);

        // Enregistrer l'utilisateur
        if (userDAO.registerUser(user)) {
            JOptionPane.showMessageDialog(frame, "Inscription réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose(); // Fermer la fenêtre d'inscription
            new Login(); // Ouvrir la fenêtre de connexion (à créer)
        } else {
            JOptionPane.showMessageDialog(frame, "Erreur lors de l'inscription", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Méthode de validation d'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Méthode de validation du mot de passe
    private boolean isValidPassword(String password) {
        return password.length() >= 5; // Vérifier que le mot de passe a au moins 5 caractères
    }
    public static void main(String[] args) {
        new Inscription();
    }
}
