package UI;

import BD.Database;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatClient {

    private JFrame frame;
    private JTextField messageField, recipientField;
    private JTextArea chatArea;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String username;

    public ChatClient(String serverAddress, String currentUser, String target) {
        frame = new JFrame("Chat Privé");
        messageField = new JTextField();
        chatArea = new JTextArea();
        chatArea.setEditable(false);

        JButton sendButton = new JButton("Envoyer");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.setSize(400, 300);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        messageField.addActionListener(e -> sendMessage(target, currentUser));
        sendButton.addActionListener(e -> sendMessage(target, currentUser));

        connectToServer(serverAddress, currentUser);
        loadChatHistory(currentUser, target);
    }

    private void loadChatHistory(String currentUser, String target) {
        List<String> messages = Database.getChatHistory(currentUser, target);
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

    private void connectToServer(String serverAddress, String currentUser) {
        try {
            socket = new Socket(serverAddress, 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(currentUser);

            new Thread(this::readMessages).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Connexion au serveur échouée", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage(String target, String currentUser) {
        String message = messageField.getText();
        if (!target.isEmpty() && !message.isEmpty()) {
            out.println(target + ":" + message);
            chatArea.append("Moi " + ": " + message + "\n");
            messageField.setText("");
            BD.Database.saveMessageToDatabase(currentUser, target, message);
        }
    }

    private void readMessages() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                chatArea.append(message + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Connexion perdue", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
