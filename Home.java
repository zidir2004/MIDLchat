package UI;

import BD.Database;
import static BD.Database.setUserOffline;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Home extends JFrame {

    private JList<String> userList;
    private JList<String> groupList;
    private JList<String> allUsersList;
    public DefaultListModel<String> userModel;
    private DefaultListModel<String> groupModel;
    private DefaultListModel<String> allUsersModel;
    private JButton refreshButton, createGroupButton, LogOutButton;
    private JLabel notificationLabel;
    private BufferedReader reader;
    private PrintWriter writer;
    private String currentUser;
    private HashMap<String, ChatClient> openChats = new HashMap<>();
    private HashMap<String, ChatGroupClient> openGroupChats = new HashMap<>();
    private DefaultListModel<String> notificationModel;
    private JList<String> notificationList;
    private JButton settingsButton;
    private JButton addFriendButton;

    public Home(String currentUser) {
        this.currentUser = currentUser;
        setTitle("Accueil - " + currentUser);
        setMinimumSize(new Dimension(900, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Modèles de liste pour les utilisateurs et groupes
        userModel = new DefaultListModel<>();
        groupModel = new DefaultListModel<>();
        allUsersModel = new DefaultListModel<>();
        notificationModel = new DefaultListModel<>();
        // Création du label de notification
        notificationLabel = new JLabel("Notifications : 0", SwingConstants.CENTER);
        notificationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        notificationLabel.setForeground(Color.RED);

        userList = new JList<>(userModel);
        groupList = new JList<>(groupModel);
        allUsersList = new JList<>(allUsersModel);
        notificationList = new JList<>(notificationModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        JScrollPane groupScrollPane = new JScrollPane(groupList);
        JScrollPane allUsersScrollPane = new JScrollPane(allUsersList);
        JScrollPane notificationScrollPane = new JScrollPane(notificationList);
        userScrollPane.setBorder(BorderFactory.createTitledBorder("Mes amis en ligne"));
        groupScrollPane.setBorder(BorderFactory.createTitledBorder("Vos groupes"));
        allUsersScrollPane.setBorder(BorderFactory.createTitledBorder("Mes amis"));

        notificationScrollPane.setBorder(BorderFactory.createTitledBorder("Notifications"));
        refreshButton = new JButton("Rafraîchir");
        createGroupButton = new JButton("Créer un groupe");
        LogOutButton = new JButton("Se déconnecter");

        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.add(userScrollPane);
        centerPanel.add(groupScrollPane);

        // Panneau à gauche pour la liste de tous les utilisateurs
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(allUsersScrollPane, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(200, getHeight())); // Définit la largeur

        Dimension buttonSize = new Dimension(180, 40);
        setButtonSize(createGroupButton, buttonSize);
        setButtonSize(refreshButton, buttonSize);
        setButtonSize(LogOutButton, buttonSize);

        // Panneau à droite pour les boutons avec une hauteur ajustée
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(220, getHeight()));
        rightPanel.add(createGroupButton);
        rightPanel.add(Box.createVerticalStrut(10)); // Espace entre les boutons
        rightPanel.add(refreshButton);
        rightPanel.add(Box.createVerticalStrut(10)); // Espace entre les boutons
        rightPanel.add(LogOutButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(notificationLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(notificationScrollPane);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        fetchOnlineUsers();
        fetchUserGroups();
        fetchAllUsers();
        fetchNotifications();
        settingsButton = new JButton("Paramètres");
        setButtonSize(settingsButton, new Dimension(180, 40));
        rightPanel.add(settingsButton);
        settingsButton.addActionListener(e -> openSettings());
        addFriendButton = new JButton("Ajouter un ami");
        setButtonSize(addFriendButton, new Dimension(180, 40));
        rightPanel.add(addFriendButton);
        changeButtonColor(addFriendButton);
        addFriendButton.addActionListener(e -> new FriendRequestsFrame(currentUser));

        // Écouteur de clic sur un utilisateur
        userList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double clic pour ouvrir un chat
                    String selectedUser = userList.getSelectedValue();
                    if (selectedUser != null && !selectedUser.equals(currentUser)) {
                        if (!openChats.containsKey(selectedUser)) {
                            ChatClient chatClient = new ChatClient("localhost", currentUser, selectedUser);
                            openChats.put(selectedUser, chatClient); // Enregistre la conversation
                        } else {
                            JOptionPane.showMessageDialog(Home.this,
                                    "Une discussion avec " + selectedUser + " est déjà ouverte.",
                                    "Discussion déjà ouverte",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });

        // Événement de clic sur une notification pour la marquer comme lue
notificationList.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 1) {
            int index = notificationList.locationToIndex(evt.getPoint());
            if (index != -1) {
                String selectedNotification = notificationModel.get(index);
                int notificationId;
                
                if (selectedNotification.contains("[Groupe:")) {
                    notificationId = extractGroupNotificationId(selectedNotification);
                } else {
                    notificationId = extractNotificationId(selectedNotification);
                }
                
                if (notificationId != -1) {
                    markNotificationAsRead(notificationId, index);
                }
            }
        }
    }
});

        allUsersList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double clic pour ouvrir un chat
                    String selectedUser = allUsersList.getSelectedValue();
                    if (selectedUser != null && !selectedUser.equals(currentUser)) {
                        if (!openChats.containsKey(selectedUser)) {
                            ChatClient chatClient = new ChatClient("localhost", currentUser, selectedUser);
                            openChats.put(selectedUser, chatClient); // Enregistre la conversation
                        } else {
                            JOptionPane.showMessageDialog(Home.this,
                                    "Une discussion avec " + selectedUser + " est déjà ouverte.",
                                    "Discussion déjà ouverte",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });

        // Écouteur de clic sur un groupe
        groupList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selectedGroup = groupList.getSelectedValue();
                    int group_id = Database.getGroupIdFromName(selectedGroup);
                    if (selectedGroup != null) {
                        if (!openGroupChats.containsKey(selectedGroup)) {
                            ChatGroupClient chatGroupClient = new ChatGroupClient("localhost", currentUser, group_id);
                            openGroupChats.put(selectedGroup, chatGroupClient);
                        } else {
                            JOptionPane.showMessageDialog(Home.this,
                                    "Une discussion avec " + selectedGroup + " est déjà ouverte.",
                                    "Discussion déjà ouverte",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });
        
        
        //modifier les paramétres du compte
        

        // Rafraîchir la liste
        refreshButton.addActionListener(e -> {
            fetchOnlineUsers();
            fetchUserGroups();
            fetchAllUsers();
            fetchNotifications();
        });

        // Se déconnecter
        LogOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setUserOffline(currentUser);
                dispatchEvent(new WindowEvent(Home.this, WindowEvent.WINDOW_CLOSING));
            }
        });

        // Créer un groupe
        createGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateGroupFrame();
            }
        });

        changeButtonColor(LogOutButton);
        changeButtonColor(createGroupButton);
        changeButtonColor(refreshButton);
        Timer timer4 = new Timer(1000, e -> fetchNotifications());
        timer4.start();
        // Rafraîchir automatiquement la liste des utilisateurs connectés toutes les 3 secondes
        Timer timer = new Timer(1000, e -> fetchOnlineUsers());
        timer.start();
        Timer timer2 = new Timer(1000, e -> fetchAllUsers());
        timer2.start();
        Timer timer3 = new Timer(1000, e -> fetchUserGroups());
        timer3.start();
        setVisible(true);
    }

    private void fetchOnlineUsers() {
        userModel.clear();

        String query = "SELECT u.username FROM users u " +
                       "JOIN amis a ON (u.id = a.utilisateur_id1 OR u.id = a.utilisateur_id2) " +
                       "WHERE a.statut = 'confirmé' AND u.status = 'online' AND u.username != ? " +
                       "AND (a.utilisateur_id1 = (SELECT id FROM users WHERE username = ?) " +
                       "OR a.utilisateur_id2 = (SELECT id FROM users WHERE username = ?))";

        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, currentUser);
            stmt.setString(2, currentUser);
            stmt.setString(3, currentUser);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userModel.addElement(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des amis en ligne", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void fetchAllUsers() {
        allUsersModel.clear();
        String query = "SELECT u.username FROM users u " +
                       "JOIN amis a ON (u.id = a.utilisateur_id1 OR u.id = a.utilisateur_id2) " +
                       "WHERE a.statut = 'confirmé' AND u.username != ? " +
                       "AND (a.utilisateur_id1 = (SELECT id FROM users WHERE username = ?) " +
                       "OR a.utilisateur_id2 = (SELECT id FROM users WHERE username = ?))";

        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, currentUser);
            stmt.setString(2, currentUser);
            stmt.setString(3, currentUser);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    allUsersModel.addElement(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération de vos amis", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void fetchUserGroups() {
        groupModel.clear();
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT cg.name FROM chat_groups cg "
                    + "JOIN group_members gm ON cg.id = gm.group_id "
                    + "JOIN users u ON gm.user_id = u.id "
                    + "WHERE u.username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, currentUser);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        groupModel.addElement(rs.getString("name"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des groupes", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSettings() {
        JDialog settingsDialog = new JDialog(this, "Modifier les paramètres", true);
        settingsDialog.setLayout(new GridLayout(3, 2, 10, 10));
        settingsDialog.setSize(400, 200);

        JLabel usernameLabel = new JLabel("Nouveau nom d'utilisateur :");
        JTextField usernameField = new JTextField(currentUser);

        JLabel passwordLabel = new JLabel("Nouveau mot de passe :");
        JPasswordField passwordField = new JPasswordField();

        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            String newUsername = usernameField.getText().trim();
            String newPassword = new String(passwordField.getPassword()).trim();

            if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
                Database.updateUserCredentials(currentUser, newUsername, newPassword);
                JOptionPane.showMessageDialog(settingsDialog, "Modifications enregistrées !");
                currentUser = newUsername;
                settingsDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(settingsDialog, "Tous les champs sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        settingsDialog.add(usernameLabel);
        settingsDialog.add(usernameField);
        settingsDialog.add(passwordLabel);
        settingsDialog.add(passwordField);
        settingsDialog.add(new JLabel()); // Espace vide
        settingsDialog.add(saveButton);

        settingsDialog.setVisible(true);
    }

    
    private void fetchNotifications() {
        notificationModel.clear();
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT s.username AS sender, m.content, n.group_message, cg.name FROM notifications n "
                    + "JOIN messages m ON n.message_id = m.id "
                    + "JOIN users s ON n.sender_id = s.id "
                    + "LEFT JOIN chat_groups cg ON m.group_id = cg.id "
                    + "WHERE n.user_id = (SELECT id FROM users WHERE username = ?) AND n.seen = false";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, currentUser);
                try (ResultSet rs = stmt.executeQuery()) {
                    int count = 0;
                    while (rs.next()) {
                        String sender = rs.getString("sender");
                        String message = rs.getString("content");
                        boolean isGroupMessage = rs.getBoolean("group_message");
                        String groupName = rs.getString("name");

                        if (isGroupMessage && groupName != null) {
                            notificationModel.addElement("[Groupe: " + groupName + "] " + sender + " a envoyé : " + message);
                        } else {
                            notificationModel.addElement(sender + " vous a envoyé un message : " + message);
                        }
                        count++;
                    }
                    notificationLabel.setText("Notifications : " + count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 // Méthode pour appliquer la couleur verte aux boutons
    private void changeButtonColor(JButton button) {
        button.setFocusPainted(false); // Supprime l'effet de focus par défaut
        button.setBorderPainted(false); // Supprime la bordure par défaut
        button.setContentAreaFilled(false); // Supprime l'effet 3D du bouton
        button.setOpaque(true); // Active le fond personnalisé

        // Changer la couleur du fond et du texte en vert
        button.setBackground(new java.awt.Color(46, 204, 113)); // Vert clair
        button.setForeground(Color.WHITE); // Texte blanc
        button.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14)); // Police moderne et lisible

        // Arrondir le bouton
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Espacement interne

        // Effet de hover (survol) : vert foncé au survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new java.awt.Color(39, 174, 96)); // Vert foncé au survol
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new java.awt.Color(46, 204, 113)); // Retour au vert clair
            }
        });
    }


    private void setButtonSize(JButton button, Dimension size) {
        button.setMaximumSize(size);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
    }

    private int extractNotificationId(String notificationText) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT n.id FROM notifications n "
                    + "JOIN messages m ON n.message_id = m.id "
                    + "JOIN users u ON m.sender_id = u.id "
                    + "WHERE u.username = ? AND m.content = ? "
                    + "AND n.user_id = (SELECT id FROM users WHERE username = ?) AND n.seen = false LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                String[] parts = notificationText.split(" vous a envoyé un message : ", 2);
                if (parts.length < 2) {
                    return -1;
                }
                stmt.setString(1, parts[0]); // Sender username
                stmt.setString(2, parts[1]); // Message content
                stmt.setString(3, currentUser); // Receiver username
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    private int extractGroupNotificationId(String notificationText) {
    try (Connection conn = Database.getConnection()) {
        String query = "SELECT n.id FROM notifications n " +
                       "JOIN messages m ON n.message_id = m.id " +
                       "JOIN chat_groups cg ON m.group_id = cg.id " +
                       "WHERE m.content = ? AND n.group_message = true " +
                       "AND n.user_id = (SELECT id FROM users WHERE username = ?) " +
                       "AND cg.name = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            String groupName = notificationText.substring(notificationText.indexOf("[") + 9, notificationText.indexOf("]"));
            String message = notificationText.substring(notificationText.lastIndexOf(":") + 2);
            stmt.setString(1, message);
            stmt.setString(2, currentUser);
            stmt.setString(3, groupName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return -1;
}


    private void markNotificationAsRead(int notificationId, int index) {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE notifications SET seen = true WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, notificationId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Supprimer immédiatement la notification de la liste
        notificationModel.remove(index);
        notificationLabel.setText("Notifications : " + notificationModel.size());
    }
    
    
}

