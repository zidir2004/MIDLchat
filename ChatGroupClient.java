package UI;

import BD.Database;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ChatGroupClient {
    private JFrame frame;
    private JTextField messageField;
    private JTextArea chatArea;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String username;
    private int groupId;
    private List<String> groupMembers;

    public ChatGroupClient(String serverAddress, String currentUser, int groupId) {
        this.username = currentUser;
        this.groupId = groupId;
        
        // Récupérer les membres du groupe
        groupMembers = Database.getGroupMembers(groupId);
        
        // Vérifier si l'utilisateur fait partie du groupe
        if (!groupMembers.contains(username)) {
            JOptionPane.showMessageDialog(null, "Vous n'êtes pas membre de ce groupe.", "Accès refusé", JOptionPane.ERROR_MESSAGE);
            return; // Quitte la fonction et empêche l'affichage de la fenêtre
        }

        frame = new JFrame("Discussion de Groupe - " + Database.getGroupNameFromId(groupId));
        messageField = new JTextField();
        chatArea = new JTextArea();
        chatArea.setEditable(false);

        JButton sendButton = new JButton("Envoyer");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.setSize(500, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        messageField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());

        connectToServer(serverAddress);
        loadChatHistory(currentUser);
    }

    private void loadChatHistory(String currentUser) {
        List<String> messages = Database.getGroupChatHistory(groupId);
        SwingUtilities.invokeLater(() -> {
            chatArea.setText("");
            for (String msg : messages) {
                String[] parts = msg.split(": ", 2); // Diviser la chaîne en deux parties : sender et message
                if (parts.length == 2) {
                    String sender = parts[0].trim();
                    String content = parts[1].trim();
                    if (sender.equals(currentUser)) {
                        sender = "Moi"; // Remplace le sender par "Moi" si c'est l'utilisateur actuel
                    }
                    chatArea.append(sender + " : " + content + "\n");
                }
            }
        });
    }

    private void connectToServer(String serverAddress) {
        try {
            socket = new Socket(serverAddress, 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("GROUP:" + groupId + ":" + username);

            // Démarrer un thread pour écouter les nouveaux messages en temps réel
            new Thread(this::readMessages).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Connexion au serveur échouée", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            // Vérifier si l'utilisateur est toujours membre du groupe
            if (!groupMembers.contains(username)) {
                JOptionPane.showMessageDialog(frame, "Vous n'êtes plus membre de ce groupe.", "Accès refusé", JOptionPane.ERROR_MESSAGE);
                return;
            }

            out.println("GROUP:" + groupId + ":" + username + ":" + message);
            chatArea.append("Moi : " + message + "\n"); // Affichage en local
            messageField.setText("");
            Database.saveGroupMessage(username, groupId, message);
        }
    }

    private void readMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("GROUP:" + groupId)) {
                    String[] parts = message.split(":", 4);
                    if (parts.length == 4) {
                        String sender = parts[2].trim();
                        String content = parts[3].trim();
                        SwingUtilities.invokeLater(() -> {
                            if (sender.equals(username)) {
                                chatArea.append("Moi : " + content + "\n");
                            } else {
                                chatArea.append(sender + " : " + content + "\n");
                            }
                        });
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Connexion perdue", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
