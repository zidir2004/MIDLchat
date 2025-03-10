import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatUI {
    private JFrame frame;
    private JPanel contactsPanel, chatPanel, topPanel, bottomPanel;
    private JTextArea chatArea;
    private JTextField messageField, searchField;
    private JButton sendButton, discussionsButton, groupsButton, profileButton, addFriendButton;
    private DefaultListModel<String> contactsModel;
    private JList<String> contactsList;
    
    private Connection connection;
    private int selectedFriendId = -1; // ID de l'ami sélectionné

    

    private int currentUserId;
    private String currentUserName;

    public ChatUI(int userId, String userName) {
        this.currentUserId = userId;
        this.currentUserName = userName;

        createAndShowGUI();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatUI(1, "Test User"));
    }

    
    public void createAndShowGUI() {
        frame = new JFrame("MIDL Chat");
        JLabel welcomeLabel = new JLabel("Bienvenue " + currentUserName);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        // Palette de couleurs
        Color backgroundColor = new Color(245, 245, 245);
        Color mainColor = new Color(212, 165, 165);
        Color buttonColor = new Color(212, 165, 165);

        // Connexion à la base de données
        connectToDatabase();

        // Panel Haut (Barre de titre + Recherche + Bienvenue)
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(mainColor);
        topPanel.setPreferredSize(new Dimension(1000, 80));

        JLabel titleLabel = new JLabel("MIDL Chat", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 60));

        searchField = new JTextField("Rechercher un utilisateur...");
        searchField.setPreferredSize(new Dimension(250, 40));
        searchField.setFont(new Font("Poppins", Font.PLAIN, 14));

        addFriendButton = new JButton("Ajouter Ami");
        addFriendButton.setBackground(buttonColor);
        addFriendButton.setForeground(Color.WHITE);
        addFriendButton.setFont(new Font("Poppins", Font.BOLD, 14));
        addFriendButton.setBorderPainted(false);
        addFriendButton.setFocusPainted(false);
        addFriendButton.addActionListener(e -> searchAndAddFriend());

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(addFriendButton, BorderLayout.EAST);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        // Barre latérale (Liste des amis)
        contactsPanel = new JPanel(new BorderLayout());
        contactsPanel.setPreferredSize(new Dimension(300, 650));
        contactsPanel.setBackground(backgroundColor);

        contactsModel = new DefaultListModel<>();
        contactsList = new JList<>(contactsModel);
        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsList.setFont(new Font("Poppins", Font.PLAIN, 18));
        contactsList.setBackground(Color.WHITE);
        contactsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedFriend = contactsList.getSelectedValue();
                if (selectedFriend != null) {
                    loadConversation(selectedFriend);
                }
            }
        });

        contactsPanel.add(new JScrollPane(contactsList), BorderLayout.CENTER);

        // Charger les amis
        loadFriends();

        // Zone de Chat
        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(Color.WHITE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Poppins", Font.PLAIN, 16));
        chatArea.setBackground(backgroundColor);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel bas (Zone d'entrée et navigation)
        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.setPreferredSize(new Dimension(1000, 120));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(backgroundColor);

        messageField = new JTextField();
        messageField.setFont(new Font("Poppins", Font.PLAIN, 16));
        sendButton = new JButton("Envoyer");
        sendButton.setBackground(buttonColor);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Poppins", Font.BOLD, 16));
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setPreferredSize(new Dimension(140, 50));

        

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        bottomPanel.add(inputPanel, BorderLayout.NORTH);

        // Ajouter les composants à la fenêtre
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(contactsPanel, BorderLayout.WEST);
        frame.add(chatPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Connexion à la base de données
    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/midlchat";
            String user = "root";
            String password = "";

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connexion à la base de données réussie !");
        } catch (Exception e) {
            System.err.println("❌ Erreur de connexion à la base de données !");
            e.printStackTrace();
        }
    }

    // Charger les amis
    private void loadFriends() {
        try {
            String query = "SELECT u.nom FROM utilisateurs u JOIN amis a ON (u.id = a.utilisateur_id1 OR u.id = a.utilisateur_id2) WHERE (a.utilisateur_id1 = ? OR a.utilisateur_id2 = ?) AND a.statut = 'confirmé' AND u.id != ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, currentUserId);
            pstmt.setInt(3, currentUserId);

            ResultSet rs = pstmt.executeQuery();
            contactsModel.clear();
            while (rs.next()) {
                contactsModel.addElement(rs.getString("nom"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des amis !");
            e.printStackTrace();
        }
    }

    // Charger la conversation avec un ami
    private void loadConversation(String friendName) {
        chatArea.setText("");  // Efface l'ancienne conversation
        try {
            String query = "SELECT contenu FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, selectedFriendId);
            pstmt.setInt(3, selectedFriendId);
            pstmt.setInt(4, currentUserId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                chatArea.append(rs.getString("contenu") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Rechercher et ajouter un ami
    private void searchAndAddFriend() {
        String friendName = searchField.getText().trim();
        try {
            String query = "INSERT INTO amis (utilisateur_id1, utilisateur_id2, statut) VALUES (?, (SELECT id FROM utilisateurs WHERE nom = ?), 'en attente')";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, currentUserId);
            pstmt.setString(2, friendName);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, friendName + " a reçu une demande d'ami !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
