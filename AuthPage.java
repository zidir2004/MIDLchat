
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AuthPage {
    private JFrame frame;
    private JTextField tfNom, tfEmail;
    private JPasswordField pfMotDePasse;
    private JButton btnAction;
    private JLabel lblSwitch;
    private boolean isLoginMode = true;

    public AuthPage() {
        frame = new JFrame("MIDL Chat - Connexion / Inscription");
        frame.setBounds(450, 200, 500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // 🎨 Couleurs et styles
        Color primaryColor = new Color(44, 62, 80); // Bleu foncé élégant
        Color secondaryColor = new Color(236, 240, 241); // Gris clair
        Color buttonColor = new Color(52, 152, 219); // Bleu vif
        Color textColor = new Color(255, 255, 255);

        // 🌟 Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(50, 50, 400, 350);
        panel.setBackground(primaryColor);
        frame.add(panel);

        // 🎤 Titre
        JLabel lblTitle = new JLabel("MIDL Chat");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(textColor);
        lblTitle.setBounds(120, 10, 200, 30);
        panel.add(lblTitle);

        // 📧 Email
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(textColor);
        lblEmail.setBounds(30, 60, 100, 20);
        panel.add(lblEmail);

        tfEmail = new JTextField();
        tfEmail.setBounds(30, 80, 340, 35);
        tfEmail.setBackground(secondaryColor);
        tfEmail.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(tfEmail);

        // 🔑 Mot de passe
        JLabel lblMotDePasse = new JLabel("Mot de Passe:");
        lblMotDePasse.setForeground(textColor);
        lblMotDePasse.setBounds(30, 130, 100, 20);
        panel.add(lblMotDePasse);

        pfMotDePasse = new JPasswordField();
        pfMotDePasse.setBounds(30, 150, 340, 35);
        pfMotDePasse.setBackground(secondaryColor);
        pfMotDePasse.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(pfMotDePasse);

        // 👤 Nom (Uniquement pour l'inscription)
        JLabel lblNom = new JLabel("Nom:");
        lblNom.setForeground(textColor);
        lblNom.setBounds(30, 200, 100, 20);
        panel.add(lblNom);

        tfNom = new JTextField();
        tfNom.setBounds(30, 220, 340, 35);
        tfNom.setBackground(secondaryColor);
        tfNom.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(tfNom);

        tfNom.setVisible(false);
        lblNom.setVisible(false);

        // 🔘 Bouton principal (Connexion / Inscription)
        btnAction = new JButton("Se connecter");
        btnAction.setBounds(30, 270, 340, 40);
        btnAction.setBackground(buttonColor);
        btnAction.setForeground(textColor);
        btnAction.setFont(new Font("Arial", Font.BOLD, 16));
        btnAction.setBorderPainted(false);
        btnAction.setFocusPainted(false);
        btnAction.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(btnAction);

        btnAction.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAction.setBackground(new Color(41, 128, 185)); // Bleu foncé au survol
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAction.setBackground(buttonColor);
            }
        });

        // 🔄 Changer de mode (Connexion <-> Inscription)
        lblSwitch = new JLabel("Pas encore de compte ? S'inscrire");
        lblSwitch.setForeground(Color.YELLOW);
        lblSwitch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblSwitch.setBounds(100, 320, 250, 20);
        panel.add(lblSwitch);

        lblSwitch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                switchMode(lblNom);
            }
        });

        // 🚀 Action du bouton principal
        btnAction.addActionListener(e -> handleAuthAction());

        frame.getContentPane().setBackground(new Color(189, 195, 199)); // Fond gris clair
        frame.setVisible(true);
    }

    // 🔄 Basculer entre connexion et inscription
    private void switchMode(JLabel lblNom) {
        isLoginMode = !isLoginMode;
        btnAction.setText(isLoginMode ? "Se connecter" : "S'inscrire");
        lblSwitch.setText(isLoginMode ? "Pas encore de compte ? S'inscrire" : "Déjà un compte ? Se connecter");

        tfNom.setVisible(!isLoginMode);
        lblNom.setVisible(!isLoginMode);
    }

    // 🏆 Gérer Connexion & Inscription
    private void handleAuthAction() {
        String email = tfEmail.getText().trim();
        String motDePasse = new String(pfMotDePasse.getPassword()).trim();
        String nom = tfNom.getText().trim();

        if (email.isEmpty() || motDePasse.isEmpty() || (!isLoginMode && nom.isEmpty())) {
            JOptionPane.showMessageDialog(frame, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        if (isLoginMode) {
            User user = userDAO.loginUser(email, motDePasse);
            if (user != null) {
                JOptionPane.showMessageDialog(frame, "Connexion réussie !");
                frame.dispose();
                new ChatUI(user.getId(), user.getNom());
            } else {
                JOptionPane.showMessageDialog(frame, "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (userDAO.registerUser(nom, email, motDePasse)) {
                JOptionPane.showMessageDialog(frame, "Inscription réussie !");
                frame.dispose();
                new ChatUI(userDAO.getUserId(email), nom);
            } else {
                JOptionPane.showMessageDialog(frame, "Erreur d'inscription.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new AuthPage();
    }
}
