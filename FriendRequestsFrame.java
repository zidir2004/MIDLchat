package UI;

import BD.Database;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FriendRequestsFrame extends JFrame {

    private JTextField searchField;
    private JButton searchButton, acceptButton, rejectButton;
    private DefaultListModel<String> requestModel;
    private JList<String> requestList;
    private String currentUser;

    public FriendRequestsFrame(String currentUser) {
        this.currentUser = currentUser;

        setTitle("Demandes d'amis");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 🔍 Recherche
        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField("Nom d'utilisateur...");
        searchButton = new JButton("Rechercher");

        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 📜 Liste des demandes
        requestModel = new DefaultListModel<>();
        requestList = new JList<>(requestModel);
        add(new JScrollPane(requestList), BorderLayout.CENTER);

        // ✅❌ Boutons accepter/refuser
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        acceptButton = new JButton("Accepter");
        rejectButton = new JButton("Refuser");

        bottomPanel.add(acceptButton);
        bottomPanel.add(rejectButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        searchButton.addActionListener(e -> sendFriendRequest());
        acceptButton.addActionListener(e -> acceptRequest());
        rejectButton.addActionListener(e -> rejectRequest());

        loadFriendRequests();
        setVisible(true);
    }

    private void loadFriendRequests() {
        requestModel.clear();
        List<String> requests = Database.getFriendRequests(currentUser);
        for (String req : requests) {
            requestModel.addElement(req);
        }
    }

    private void sendFriendRequest() {
        String target = searchField.getText().trim();
        if (target.equals(currentUser)) {
            JOptionPane.showMessageDialog(this, "Vous ne pouvez pas vous ajouter vous-même.");
            return;
        }
        if (!target.isEmpty()) {
            boolean success = Database.sendFriendRequest(currentUser, target);
            if (success) {
                JOptionPane.showMessageDialog(this, "Demande envoyée à " + target);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur : utilisateur non trouvé ou déjà demandé.");
            }
        }
    }

    private void acceptRequest() {
        String sender = requestList.getSelectedValue();
        if (sender != null) {
            Database.acceptFriendRequest(currentUser, sender);
            JOptionPane.showMessageDialog(this, sender + " est maintenant votre ami !");
            loadFriendRequests();
        }
    }

    private void rejectRequest() {
        String sender = requestList.getSelectedValue();
        if (sender != null) {
            Database.rejectFriendRequest(currentUser, sender);
            JOptionPane.showMessageDialog(this, "Demande de " + sender + " refusée.");
            loadFriendRequests();
        }
    }
}
