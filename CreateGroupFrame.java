package UI;

import BD.Database;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CreateGroupFrame extends JFrame {
    private JTextField groupNameField;
    private JList<String> userList;
    private DefaultListModel<String> userModel;
    private JButton createButton;

    public CreateGroupFrame() {
        setTitle("Créer un Groupe");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Champ pour entrer le nom du groupe
        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel groupNameLabel = new JLabel("Nom du groupe:");
        groupNameField = new JTextField(20);
        topPanel.add(groupNameLabel);
        topPanel.add(groupNameField);

        // Liste des utilisateurs disponibles avec sélection multiple
        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Permet la sélection multiple
        userList.setVisibleRowCount(10); // Définit la hauteur pour voir plusieurs utilisateurs
        loadUsers();

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setPreferredSize(new Dimension(350, 200)); // Ajuste la taille pour une meilleure visibilité
        scrollPane.setBorder(BorderFactory.createTitledBorder("Sélectionnez les utilisateurs"));

        // Bouton de création du groupe
        createButton = new JButton("Créer Groupe");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createGroup();
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(createButton, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    private void loadUsers() {
        SwingUtilities.invokeLater(() -> {
            try (Connection conn = Database.getConnection()) {
                String query = "SELECT username FROM users";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                userModel.clear(); // Assurer une mise à jour correcte
                while (rs.next()) {
                    userModel.addElement(rs.getString("username"));
                }

                if (userModel.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Aucun utilisateur trouvé.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement des utilisateurs", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void createGroup() {
        String groupName = groupNameField.getText().trim();
        List<String> selectedUsers = userList.getSelectedValuesList();

        if (groupName.isEmpty() || selectedUsers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un nom de groupe et sélectionner des utilisateurs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = Database.getConnection()) {
            // Insérer le groupe dans chat_groups
            String insertGroupQuery = "INSERT INTO chat_groups (name) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(insertGroupQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, groupName);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            int groupId = -1;
            if (rs.next()) {
                groupId = rs.getInt(1);
            }

            // Insérer les membres dans group_members
            String insertMemberQuery = "INSERT INTO group_members (user_id, group_id, role) VALUES ((SELECT id FROM users WHERE username = ?), ?, 'member')";
            PreparedStatement memberStmt = conn.prepareStatement(insertMemberQuery);

            for (String user : selectedUsers) {
                memberStmt.setString(1, user);
                memberStmt.setInt(2, groupId);
                memberStmt.addBatch();
            }
            memberStmt.executeBatch();

            JOptionPane.showMessageDialog(this, "Groupe créé avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la création du groupe", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
