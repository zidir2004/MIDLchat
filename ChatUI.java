import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatUI {
    private JFrame frame;
    private JPanel chatPanel;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private DefaultListModel<String> friendsModel;
    private JList<String> friendsList;
    private int currentUserId;
    private String currentUserName;
    private int selectedFriendId = -1;
    private Connection connection;
    private Timer refreshTimer;
    private Map<String, Integer> friendIdMap = new HashMap<>();

    public ChatUI(int userId, String userName) {
        this.currentUserId = userId;
        this.currentUserName = userName;
        this.connection = DatabaseConnection.getConnection();

        frame = new JFrame("MIDL Chat - " + userName);
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 🎨 Palette de couleurs
        Color sidebarColor = new Color(52, 73, 94);
        Color buttonColor = new Color(46, 204, 113);
        Color textColor = Color.WHITE;
        Color chatBackground = new Color(245, 245, 245); // Fond gris clair pour la zone de chat

        // 🏷️ En-tête
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(sidebarColor);
        topPanel.setPreferredSize(new Dimension(900, 50));

        JLabel titleLabel = new JLabel("Bienvenue, " + userName + " !");
        titleLabel.setForeground(textColor);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Déconnexion et paramètres
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(buttonColor);
        logoutButton.setPreferredSize(new Dimension(120, 40));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutButton.setOpaque(true);
        logoutButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        logoutButton.addActionListener(e -> {
            frame.dispose();
            new AuthPage();
        });

        JButton settingsButton = new JButton("Paramètres");
        settingsButton.setFont(new Font("Arial", Font.BOLD, 14));
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setBackground(buttonColor);
        settingsButton.setPreferredSize(new Dimension(120, 40));
        settingsButton.setFocusPainted(false);
        settingsButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        settingsButton.setOpaque(true);
        settingsButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        settingsButton.addActionListener(e -> openSettings());

        buttonsPanel.add(settingsButton);
        buttonsPanel.add(logoutButton);

        topPanel.add(buttonsPanel, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);

        // 📜 Liste des amis + Ajout d'amis
        JPanel friendsPanel = new JPanel(new BorderLayout());
        friendsPanel.setBackground(sidebarColor);
        friendsPanel.setPreferredSize(new Dimension(250, 600));

        JLabel friendsLabel = new JLabel("👥 Amis");
        friendsLabel.setForeground(textColor);
        friendsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        friendsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        friendsPanel.add(friendsLabel, BorderLayout.NORTH);

        friendsModel = new DefaultListModel<>();
        friendsList = new JList<>(friendsModel);
        friendsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendsPanel.add(new JScrollPane(friendsList), BorderLayout.CENTER);

        friendsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedFriend = friendsList.getSelectedValue();
                if (selectedFriend != null) {
                    loadConversation(selectedFriend);
                }
            }
        });

        loadFriends(); // Charger la liste des amis

        // 📧 Bouton pour ouvrir la gestion des demandes d'amis
        JButton addFriendButton = new JButton("Ajouter un ami");
        addFriendButton.setBackground(buttonColor);
        addFriendButton.setForeground(Color.WHITE);
        addFriendButton.addActionListener(e -> new FriendRequestsUI(currentUserId));

        friendsPanel.add(addFriendButton, BorderLayout.SOUTH);
        frame.add(friendsPanel, BorderLayout.WEST);

        // 💬 Zone de Chat
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(chatBackground);

        JScrollPane scrollPane = new JScrollPane(chatPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // ✍️ Zone d'Entrée et Bouton Envoyer
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.LIGHT_GRAY);
        bottomPanel.setPreferredSize(new Dimension(900, 50));

        messageField = new JTextField();
        sendButton = new JButton("Envoyer");
        sendButton.setBackground(buttonColor);
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(e -> sendMessage());

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // 🔄 Rafraîchir le chat toutes les 2 secondes
        refreshTimer = new Timer(2000, e -> {
        	loadFriends();
            if (selectedFriendId != -1) {
                loadConversation(friendsList.getSelectedValue());
            }
        });
        refreshTimer.start();
    }

    // 🔄 Charger uniquement les amis confirmés
    private void loadFriends() {
        try {
            String query = "SELECT u.id, u.nom FROM utilisateurs u " +
                           "JOIN amis a ON (u.id = a.utilisateur_id1 OR u.id = a.utilisateur_id2) " +
                           "WHERE (a.utilisateur_id1 = ? OR a.utilisateur_id2 = ?) " +
                           "AND a.statut = 'confirmé' " +
                           "AND u.id != ?"; // Exclure l'utilisateur lui-même

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, currentUserId);
            pstmt.setInt(3, currentUserId);

            ResultSet rs = pstmt.executeQuery();
            friendsModel.clear();
            friendIdMap.clear();
            while (rs.next()) {
                String friendName = "👤 " + rs.getString("nom");
                friendsModel.addElement(friendName);
                friendIdMap.put(friendName, rs.getInt("id")); // Stocker l'ID de l'ami
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔄 Charger la conversation avec un ami
    private void loadConversation(String friendName) {
        chatPanel.removeAll();  // Effacer les anciens messages
        selectedFriendId = friendIdMap.get(friendName);

        try {
            String query = "SELECT sender_id, contenu, timestamp FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, selectedFriendId);
            pstmt.setInt(3, selectedFriendId);
            pstmt.setInt(4, currentUserId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int senderId = rs.getInt("sender_id");
                String sender = (senderId == currentUserId) ? "Moi" : friendName;
                String content = rs.getString("contenu");
                String timestamp = rs.getString("timestamp");
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String formattedTime = sdf.format(Timestamp.valueOf(timestamp));

                // Créer des bulles de messages avec des couleurs et arrondis
                JPanel messagePanel = new JPanel();
                messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));

                // Définir les couleurs et alignements
                if (sender.equals("Moi")) {
                    messagePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    messagePanel.setBackground(new Color(46, 204, 113)); // Vert
                } else {
                    messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    messagePanel.setBackground(new Color(189, 195, 199)); // Gris
                }

                // Ajouter le nom de l'expéditeur au-dessus du message
                JPanel namePanel = new JPanel();
                namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
                namePanel.setBackground(new Color(0, 0, 0, 0)); // Transparent
                JLabel nameLabel = new JLabel(sender);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
                nameLabel.setForeground(Color.WHITE);
                namePanel.add(nameLabel);

                // Message avec bulles arrondies
                JLabel messageLabel = new JLabel("<html><p style='width: 250px; padding: 10px;'>" + content + " <br><span style='font-size:10px;'>" + formattedTime + "</span></p></html>");
                messageLabel.setForeground(Color.WHITE);
                messageLabel.setBackground(new Color(0, 0, 0, 0)); // Transparence pour le fond

                messagePanel.add(namePanel);
                messagePanel.add(messageLabel);
                messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Espacement
                messagePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Bordure du message

                chatPanel.add(messagePanel);
            }
            chatPanel.revalidate(); // Revalidate the panel after adding new components
            chatPanel.repaint(); // Repaint to update the display
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ouvrir la fenêtre des paramètres
    private void openSettings() {
        // Créer une nouvelle fenêtre modale
        JDialog settingsDialog = new JDialog(frame, "Paramètres", true);
        settingsDialog.setLayout(new GridLayout(4, 2, 10, 10));
        settingsDialog.setSize(400, 200);

        // Ajouter des champs pour le nom, l'email et le mot de passe
        JLabel nameLabel = new JLabel("Nom : ");
        JTextField nameField = new JTextField(currentUserName);
        JLabel emailLabel = new JLabel("Email : ");
        JTextField emailField = new JTextField(getEmailFromDB());
        JLabel passwordLabel = new JLabel("Mot de passe : ");
        JPasswordField passwordField = new JPasswordField();

        JButton saveButton = new JButton("Sauvegarder");
        saveButton.addActionListener(e -> saveSettings(nameField.getText(), emailField.getText(), new String(passwordField.getPassword())));

        settingsDialog.add(nameLabel);
        settingsDialog.add(nameField);
        settingsDialog.add(emailLabel);
        settingsDialog.add(emailField);
        settingsDialog.add(passwordLabel);
        settingsDialog.add(passwordField);
        settingsDialog.add(saveButton);

        settingsDialog.setVisible(true);
    }

    // Méthode pour récupérer l'email de la base de données
    private String getEmailFromDB() {
        String email = "";
        try {
            String query = "SELECT email FROM utilisateurs WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                email = rs.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return email;
    }

    // Sauvegarder les paramètres modifiés
    private void saveSettings(String name, String email, String password) {
        try {
            String query = "UPDATE utilisateurs SET nom = ?, email = ?, mot_de_passe = ? WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setInt(4, currentUserId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Paramètres sauvegardés avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 📨 Envoyer un message
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && selectedFriendId != -1) {
            try {
                String query = "INSERT INTO messages (sender_id, receiver_id, contenu, timestamp) VALUES (?, ?, ?, NOW())";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, currentUserId);
                pstmt.setInt(2, selectedFriendId);
                pstmt.setString(3, message);
                pstmt.executeUpdate();

                // Affichage direct du message
                JPanel messagePanel = new JPanel();
                messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
                messagePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
                messagePanel.setBackground(new Color(46, 204, 113)); // Vert

                JLabel messageLabel = new JLabel("<html><p style='width: 250px; padding: 10px;'>" + message + " <br><span style='font-size:10px;'>" + getCurrentTime() + "</span></p></html>");
                messageLabel.setForeground(Color.WHITE);
                messageLabel.setBackground(new Color(0, 0, 0, 0)); // Transparence pour le fond

                messagePanel.add(messageLabel);
                messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Espacement

                chatPanel.add(messagePanel);
                chatPanel.revalidate(); // Revalidate the panel after adding new components
                chatPanel.repaint(); // Repaint to update the display

                messageField.setText("");

                // Rafraîchir la conversation après l'envoi du message
                loadConversation(friendsList.getSelectedValue());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour obtenir l'heure actuelle dans le format souhaité
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }
}
