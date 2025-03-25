package BD;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import UTILS.Hashage;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/ChatApp?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) {
            throw new SQLException("❌ Driver MySQL introuvable !", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveMessageToDatabase(String sender, String receiver, String content) {
        String query = "INSERT INTO messages (sender_id, receiver_id, content, timestamp) VALUES ((SELECT id FROM users WHERE username = ?), (SELECT id FROM users WHERE username = ?), ?, NOW())";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sender);
            stmt.setString(2, receiver);
            stmt.setString(3, content);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getChatHistory(String user1, String user2) {
        List<String> chatHistory = new ArrayList<>();
        String query = "SELECT u.username AS sender, m.content, m.timestamp "
                + "FROM messages m "
                + "JOIN users u ON m.sender_id = u.id "
                + "WHERE (m.sender_id = (SELECT id FROM users WHERE username = ?) AND "
                + "       m.receiver_id = (SELECT id FROM users WHERE username = ?)) "
                + "   OR (m.sender_id = (SELECT id FROM users WHERE username = ?) AND "
                + "       m.receiver_id = (SELECT id FROM users WHERE username = ?)) "
                + "ORDER BY m.timestamp ASC";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.setString(3, user2);
            stmt.setString(4, user1);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender");
                String content = rs.getString("content");
                chatHistory.add(sender + ": " + content);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatHistory;
    }

    public static void saveNotification(String receiver, int messageId) {
        String query = "INSERT INTO notifications (message_id, user_id, seen) "
                + "VALUES (?, (SELECT id FROM users WHERE username = ?), false)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, messageId);
            stmt.setString(2, receiver);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setUserOffline(String username) {
        String query = "UPDATE users SET status = 'offline' WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveGroupMessage(String sender, int groupId, String content) {
        String query = "INSERT INTO messages (sender_id, group_id, content, timestamp) VALUES ((SELECT id FROM users WHERE username = ?), ?, ?, NOW())";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sender);
            stmt.setInt(2, groupId);
            stmt.setString(3, content);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getGroupChatHistory(int groupId) {
        List<String> chatHistory = new ArrayList<>();
        String query = "SELECT u.username AS sender, m.content, m.timestamp "
                + "FROM messages m "
                + "JOIN users u ON m.sender_id = u.id "
                + "WHERE m.group_id = ? "
                + "ORDER BY m.timestamp ASC";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender");
                String content = rs.getString("content");
                chatHistory.add(sender + ": " + content);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatHistory;
    }
    
    public static void updateUserCredentials(String oldUsername, String newUsername, String newPassword) {
        String query = "UPDATE users SET username = ?, password = ? WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newUsername);
            stmt.setString(2, Hashage.hashPassword(newPassword));
            stmt.setString(3, oldUsername);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public static boolean sendFriendRequest(String fromUsername, String toUsername) {
        String query = "INSERT INTO amis (utilisateur_id1, utilisateur_id2, statut) " +
                       "VALUES ((SELECT id FROM users WHERE username = ?), (SELECT id FROM users WHERE username = ?), 'en attente')";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fromUsername);
            stmt.setString(2, toUsername);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> getFriendRequests(String username) {
        List<String> demandes = new ArrayList<>();
        String query = "SELECT u.username FROM amis a JOIN users u ON a.utilisateur_id1 = u.id " +
                       "WHERE a.utilisateur_id2 = (SELECT id FROM users WHERE username = ?) AND a.statut = 'en attente'";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                demandes.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return demandes;
    }

    public static void acceptFriendRequest(String currentUser, String sender) {
        String query = "UPDATE amis SET statut = 'confirmé' WHERE utilisateur_id1 = (SELECT id FROM users WHERE username = ?) " +
                       "AND utilisateur_id2 = (SELECT id FROM users WHERE username = ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sender);
            stmt.setString(2, currentUser);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void rejectFriendRequest(String currentUser, String sender) {
        String query = "DELETE FROM amis WHERE utilisateur_id1 = (SELECT id FROM users WHERE username = ?) " +
                       "AND utilisateur_id2 = (SELECT id FROM users WHERE username = ?) AND statut = 'en attente'";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sender);
            stmt.setString(2, currentUser);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public static List<String> getConfirmedFriends(String username) {
        List<String> friends = new ArrayList<>();
        String query = "SELECT u.username FROM users u " +
                       "JOIN amis a ON (u.id = a.utilisateur_id1 OR u.id = a.utilisateur_id2) " +
                       "WHERE a.statut = 'confirmé' AND u.username != ? " +
                       "AND (a.utilisateur_id1 = (SELECT id FROM users WHERE username = ?) OR " +
                       "     a.utilisateur_id2 = (SELECT id FROM users WHERE username = ?))";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, username);
            stmt.setString(3, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                friends.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    
    
    public static List<String> getGroupMembers(int groupId) {
        List<String> members = new ArrayList<>();
        String query = "SELECT u.username FROM users u "
                + "JOIN group_members gm ON u.id = gm.user_id "
                + "WHERE gm.group_id = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public static int getGroupIdFromName(String groupName) {
        String query = "SELECT id FROM chat_groups WHERE name = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getGroupNameFromId(int group_id) {
        String query = "SELECT name FROM chat_groups WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, group_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void storeNotification(String sender, String recipient, String content) {
        try (Connection conn = Database.getConnection()) {
            String query = "INSERT INTO notifications (message_id, user_id, sender_id, seen) "
                    + "VALUES ((SELECT id FROM messages WHERE content = ? ORDER BY timestamp DESC LIMIT 1), "
                    + "(SELECT id FROM users WHERE username = ?), "
                    + "(SELECT id FROM users WHERE username = ?), false)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, content);
                stmt.setString(2, recipient);
                stmt.setString(3, sender);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void storeNotificationGroup(String sender, String recipient, String content) {
    try (Connection conn = Database.getConnection()) {
        String query = "INSERT INTO notifications (message_id, user_id, sender_id, seen, group_message) "
                + "VALUES ((SELECT id FROM messages WHERE content = ? ORDER BY timestamp DESC LIMIT 1), "
                + "(SELECT id FROM users WHERE username = ?), "
                + "(SELECT id FROM users WHERE username = ?), false, true)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, content);
            stmt.setString(2, recipient);
            stmt.setString(3, sender);
            stmt.executeUpdate();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
