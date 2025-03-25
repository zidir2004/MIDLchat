package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import BD.Database;
import SERVER.ChatServer;
import UTILS.Hashage;
import java.io.*;
import java.net.Socket;
import java.sql.*;

public class AuthPage {
    private JFrame frame;
    private JTextField tfNom;
    private JTextField tfUsername;
    private JPasswordField pfMotDePasse;
    private JButton btnAction;
    private JLabel lblSwitch;
    private boolean isLoginMode = true;

    public AuthPage() {
        frame = new JFrame("ChatApp - Connexion / Inscription");
        frame.setBounds(450, 200, 500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        startServer();

        Color primaryColor = new Color(44, 62, 80);
        Color secondaryColor = new Color(236, 240, 241);
        Color buttonColor = new Color(52, 152, 219);
        Color textColor = new Color(255, 255, 255);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(50, 50, 400, 350);
        panel.setBackground(primaryColor);
        frame.add(panel);

        JLabel lblTitle = new JLabel("ChatApp");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(textColor);
        lblTitle.setBounds(120, 10, 200, 30);
        panel.add(lblTitle);

        JLabel lblUsername = new JLabel("Nom d'utilisateur:");
        lblUsername.setForeground(textColor);
        lblUsername.setBounds(30, 60, 150, 20);
        panel.add(lblUsername);

        tfUsername = new JTextField();
        tfUsername.setBounds(30, 80, 340, 35);
        tfUsername.setBackground(secondaryColor);
        tfUsername.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(tfUsername);

        JLabel lblMotDePasse = new JLabel("Mot de passe:");
        lblMotDePasse.setForeground(textColor);
        lblMotDePasse.setBounds(30, 130, 150, 20);
        panel.add(lblMotDePasse);

        pfMotDePasse = new JPasswordField();
        pfMotDePasse.setBounds(30, 150, 340, 35);
        pfMotDePasse.setBackground(secondaryColor);
        pfMotDePasse.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(pfMotDePasse);

        JLabel lblNom = new JLabel("Nom complet:");
        lblNom.setForeground(textColor);
        lblNom.setBounds(30, 200, 150, 20);
        panel.add(lblNom);

        tfNom = new JTextField();
        tfNom.setBounds(30, 220, 340, 35);
        tfNom.setBackground(secondaryColor);
        tfNom.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(tfNom);

        tfNom.setVisible(false);
        lblNom.setVisible(false);

        btnAction = new JButton("Se connecter");
        btnAction.setBounds(30, 270, 340, 40);
        btnAction.setBackground(buttonColor);
        btnAction.setForeground(textColor);
        btnAction.setFont(new Font("Arial", Font.BOLD, 16));
        btnAction.setBorderPainted(false);
        btnAction.setFocusPainted(false);
        btnAction.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(btnAction);

        btnAction.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnAction.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(MouseEvent evt) {
                btnAction.setBackground(buttonColor);
            }
        });

        lblSwitch = new JLabel("Pas encore de compte ? S'inscrire");
        lblSwitch.setForeground(Color.YELLOW);
        lblSwitch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblSwitch.setBounds(100, 320, 250, 20);
        panel.add(lblSwitch);

        lblSwitch.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                switchMode(lblNom);
            }
        });

        btnAction.addActionListener(e -> handleAuthAction());

        frame.getContentPane().setBackground(new Color(189, 195, 199));
        frame.setVisible(true);
    }

    private void startServer() {
        new Thread(() -> ChatServer.main(new String[]{})).start();
    }

    private void switchMode(JLabel lblNom) {
        isLoginMode = !isLoginMode;
        btnAction.setText(isLoginMode ? "Se connecter" : "S'inscrire");
        lblSwitch.setText(isLoginMode ? "Pas encore de compte ? S'inscrire" : "Déjà un compte ? Se connecter");
        tfNom.setVisible(!isLoginMode);
        lblNom.setVisible(!isLoginMode);
    }

    private void handleAuthAction() {
        String username = tfUsername.getText().trim();
        String password = new String(pfMotDePasse.getPassword()).trim();
        String nom = tfNom.getText().trim();

        if (username.isEmpty() || password.isEmpty() || (!isLoginMode && nom.isEmpty())) {
            JOptionPane.showMessageDialog(frame, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isLoginMode) {
            try (Connection conn = Database.getConnection()) {
                String query = "SELECT password FROM users WHERE username = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, username);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String storedHashedPassword = rs.getString("password");
                            if (Hashage.comparePassword(password, storedHashedPassword)) {
                                String updateQuery = "UPDATE users SET status = 'online' WHERE username = ?";
                                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                    updateStmt.setString(1, username);
                                    updateStmt.executeUpdate();
                                }
                                connectToServer(username);
                                JOptionPane.showMessageDialog(frame, "Connexion réussie !");
                                System.out.println("Connexion OK. Ouverture de Home pour : " + username);
                                Home home = new Home(username);
                                home.setVisible(true);
                                frame.dispose();
                            } else {
                                JOptionPane.showMessageDialog(frame, "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Erreur de connexion à la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            try (Connection conn = Database.getConnection()) {
                String hashedPassword = Hashage.hashPassword(password);
                String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, username);
                    stmt.setString(2, hashedPassword);
                    stmt.executeUpdate();

                    String updateQuery = "UPDATE users SET status = 'online' WHERE username = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, username);
                        updateStmt.executeUpdate();
                    }
                    connectToServer(username);
                    JOptionPane.showMessageDialog(frame, "Inscription réussie !");
                    Home home = new Home(username);
                    home.setVisible(true);
                    frame.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de l'inscription.", "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public void connectToServer(String username) {
        try {
            Socket socket = new Socket("localhost", 12345);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.println("LOGIN|" + username);
            String response = reader.readLine();
            if (!"OK".equals(response)) {
                System.out.println("Erreur lors de l'ajout de l'utilisateur au serveur.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AuthPage();
    }
}
