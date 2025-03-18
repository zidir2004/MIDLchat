import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class FriendRequestsUI {
    private JFrame frame;
    private DefaultListModel<String> requestsModel;
    private JList<String> requestsList;
    private int currentUserId;
    private Connection connection;
    private JTextField searchField;
    private JButton searchButton, acceptButton, rejectButton;

    public FriendRequestsUI(int userId) {
        this.currentUserId = userId;
        this.connection = DatabaseConnection.getConnection();

        frame = new JFrame("Demandes d'amis");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 🎨 Style
        Color buttonColor = new Color(46, 204, 113);

        // 🔍 Barre de recherche pour ajouter un ami
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField("Entrez un nom ou email...");
        searchButton = new JButton("Rechercher");
        searchButton.setBackground(buttonColor);
        searchButton.setForeground(Color.WHITE);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        frame.add(searchPanel, BorderLayout.NORTH);

        // 📜 Liste des demandes reçues
        requestsModel = new DefaultListModel<>();
        requestsList = new JList<>(requestsModel);
        frame.add(new JScrollPane(requestsList), BorderLayout.CENTER);

        // 🔘 Boutons d'acceptation et de refus
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        acceptButton = new JButton("Accepter");
        rejectButton = new JButton("Refuser");

        acceptButton.setBackground(buttonColor);
        rejectButton.setBackground(Color.RED);
        acceptButton.setForeground(Color.WHITE);
        rejectButton.setForeground(Color.WHITE);

        buttonsPanel.add(acceptButton);
        buttonsPanel.add(rejectButton);
        frame.add(buttonsPanel, BorderLayout.SOUTH);

        // Charger les demandes
        loadFriendRequests();

        // 🔄 Ajouter un ami en recherchant par email
        searchButton.addActionListener(e -> addFriendBySearch());

        // ✅ Accepter une demande
        acceptButton.addActionListener(e -> acceptFriendRequest());

        // ❌ Refuser une demande
        rejectButton.addActionListener(e -> rejectFriendRequest());

        frame.setVisible(true);
    }

    // 🔄 Charger les demandes d'amis
    private void loadFriendRequests() {
        try {
            String query = "SELECT u.nom FROM utilisateurs u JOIN amis a ON u.id = a.utilisateur_id1 " +
                           "WHERE a.utilisateur_id2 = ? AND a.statut = 'en attente'";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            requestsModel.clear();
            while (rs.next()) {
                requestsModel.addElement(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔍 Ajouter un ami en recherchant par nom ou email
    private void addFriendBySearch() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Veuillez entrer un nom ou un email !");
            return;
        }

        try {
            String query = "INSERT INTO amis (utilisateur_id1, utilisateur_id2, statut) VALUES (?, (SELECT id FROM utilisateurs WHERE email = ? OR nom = ?), 'en attente')";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, currentUserId);
            pstmt.setString(2, searchQuery);
            pstmt.setString(3, searchQuery);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(frame, "Demande envoyée !");
            } else {
                JOptionPane.showMessageDialog(frame, "Utilisateur non trouvé !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Accepter une demande d'ami
    private void acceptFriendRequest() {
        String selectedFriend = requestsList.getSelectedValue();
        if (selectedFriend == null) {
            JOptionPane.showMessageDialog(frame, "Sélectionnez une demande à accepter !");
            return;
        }

        try {
            String query = "UPDATE amis SET statut = 'confirmé' WHERE utilisateur_id1 = (SELECT id FROM utilisateurs WHERE nom = ?) " +
                           "AND utilisateur_id2 = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, selectedFriend);
            pstmt.setInt(2, currentUserId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(frame, selectedFriend + " est maintenant votre ami !");
            requestsModel.removeElement(selectedFriend);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ❌ Refuser une demande d'ami
    private void rejectFriendRequest() {
        String selectedFriend = requestsList.getSelectedValue();
        if (selectedFriend == null) {
            JOptionPane.showMessageDialog(frame, "Sélectionnez une demande à refuser !");
            return;
        }

        try {
            String query = "DELETE FROM amis WHERE utilisateur_id1 = (SELECT id FROM utilisateurs WHERE nom = ?) " +
                           "AND utilisateur_id2 = ? AND statut = 'en attente'";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, selectedFriend);
            pstmt.setInt(2, currentUserId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Demande de " + selectedFriend + " refusée.");
            requestsModel.removeElement(selectedFriend);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
