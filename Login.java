

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Login {

    private JFrame frame;
    private JTextField tfEmail;
    private JPasswordField pfMotDePasse;
    private JButton btnLogin;

    public Login() {
        // Créer le cadre de la fenêtre
        frame = new JFrame("Connexion");
        frame.setBounds(400, 200, 450, 300); // Taille de la fenêtre agrandie
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Définir un layout de type GridBagLayout pour plus de flexibilité
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espacement entre les composants

        // Changer la couleur de fond en rose
        frame.getContentPane().setBackground(new Color(255, 182, 193));

        // Ajouter des labels
        JLabel lblEmail = new JLabel("Email : ");
        JLabel lblMotDePasse = new JLabel("Mot de Passe : ");
        
        // Personnaliser les labels pour une meilleure lisibilité
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMotDePasse.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Créer les champs de texte
        tfEmail = new JTextField(20);
        pfMotDePasse = new JPasswordField(20);
        
        // Personnaliser les champs de texte
        tfEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        pfMotDePasse.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Créer un bouton de connexion
        btnLogin = new JButton("Se connecter");
        btnLogin.setBackground(new Color(255, 105, 180)); // Couleur rose clair
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(150, 40));
        
        // Personnaliser l'apparence du bouton
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createRaisedBevelBorder());

        // Ajouter les composants à la fenêtre avec un layout flexible
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(lblEmail, gbc);

        gbc.gridx = 1;
        frame.add(tfEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(lblMotDePasse, gbc);

        gbc.gridx = 1;
        frame.add(pfMotDePasse, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(btnLogin, gbc);

        // Ajouter un ActionListener pour le bouton
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connecterUtilisateur();
            }
        });

        // Rendre la fenêtre visible
        frame.setVisible(true);
    }

    private void connecterUtilisateur() {
        String email = tfEmail.getText();
        String motDePasse = new String(pfMotDePasse.getPassword());

        // Vérification si les champs sont vides
        if (email.isEmpty() || motDePasse.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Tous les champs doivent être remplis", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();

        // Vérifier que l'email existe et que le mot de passe est correct
        if (userDAO.emailExists(email) && userDAO.checkPassword(email, motDePasse)) {
            JOptionPane.showMessageDialog(frame, "Connexion réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            // Ouvrir la fenêtre principale de chat (à créer)
        } else {
            JOptionPane.showMessageDialog(frame, "Identifiants incorrects", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}