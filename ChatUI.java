import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;

public class ChatUI {
    private JFrame frame;
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

        // 🏷️ En-tête
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(sidebarColor);
        topPanel.setPreferredSize(new Dimension(900, 50));

        JLabel titleLabel = new JLabel("Bienvenue, " + userName + " !");
        titleLabel.setForeground(textColor);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setBackground(buttonColor);
        logoutButton.setForeground(Color.WHITE);
        topPanel.add(logoutButton, BorderLayout.EAST);
        logoutButton.addActionListener(e -> {
            frame.dispose();
            new AuthPage();
        });

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
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(Color.WHITE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        frame.add(chatPanel, BorderLayout.CENTER);

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
        chatArea.setText("");  // Efface les anciens messages
        selectedFriendId = friendIdMap.get(friendName);

        try {
            String query = "SELECT sender_id, contenu FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, selectedFriendId);
            pstmt.setInt(3, selectedFriendId);
            pstmt.setInt(4, currentUserId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int senderId = rs.getInt("sender_id");
                String sender = (senderId == currentUserId) ? "Moi" : friendName;
                chatArea.append(sender + ": " + rs.getString("contenu") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 📨 Envoyer un message
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && selectedFriendId != -1) {
            try {
                String query = "INSERT INTO messages (sender_id, receiver_id, contenu) VALUES (?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, currentUserId);
                pstmt.setInt(2, selectedFriendId);
                pstmt.setString(3, message);
                pstmt.executeUpdate();

                chatArea.append("Moi: " + message + "\n");
                messageField.setText("");

                // Rafraîchir la conversation après l'envoi du message
                loadConversation(friendsList.getSelectedValue());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
