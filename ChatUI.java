package midlchat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatUI {
    private JFrame frame;
    private JPanel contactsPanel, chatPanel, topPanel, bottomPanel;
    private JTextArea chatArea;
    private JTextField messageField, searchField;
    private JButton sendButton, discussionsButton, groupsButton, profileButton;
    private DefaultListModel<String> contactsModel;
    private JList<String> contactsList;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatUI().createAndShowGUI());
    }

    public void createAndShowGUI() {
        frame = new JFrame("MIDL Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700); // Taille plus spacieuse
        frame.setLayout(new BorderLayout());

        // Palette de couleurs
        Color backgroundColor = new Color(245, 245, 245); // Blanc cassé
        Color mainColor = new Color(212, 165, 165); // Rose poudré
        Color buttonColor = new Color(212, 165, 165); // Rose poudré
        Color textColor = new Color(176, 176, 176); // Gris clair pour le texte

        // Panel Haut (Barre de titre + Recherche + Bienvenue)
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(mainColor);
        topPanel.setPreferredSize(new Dimension(1000, 80));

        JLabel titleLabel = new JLabel("MIDL Chat", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 60));

        searchField = new JTextField("Rechercher...");
        searchField.setPreferredSize(new Dimension(250, 40));
        searchField.setFont(new Font("Poppins", Font.PLAIN, 14));

        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(mainColor);
        JLabel welcomeLabel = new JLabel("Bienvenue Louiza");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Poppins", Font.BOLD, 18));

        JLabel profileIcon = new JLabel(new ImageIcon("profile_icon.png"));
        profileIcon.setPreferredSize(new Dimension(40, 40));

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(profileIcon);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(welcomePanel, BorderLayout.EAST);

        // Barre latérale (Liste des contacts avec avatars)
        contactsPanel = new JPanel();
        contactsPanel.setLayout(new BorderLayout());
        contactsPanel.setPreferredSize(new Dimension(300, 650));
        contactsPanel.setBackground(backgroundColor);

        contactsModel = new DefaultListModel<>();
        contactsModel.addElement("🧑‍🦰 Louis");
        contactsModel.addElement("👩‍🦱 Dorsaf");
        contactsModel.addElement("👨‍🦱 Idir");
        contactsList = new JList<>(contactsModel);
        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsList.setFont(new Font("Poppins", Font.PLAIN, 18));
        contactsList.setBackground(Color.WHITE);
        contactsPanel.add(new JScrollPane(contactsList), BorderLayout.CENTER);

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

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        JPanel navPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        navPanel.setBackground(backgroundColor);

        discussionsButton = createStyledButton("Discussions");
        groupsButton = createStyledButton("Groupes");
        profileButton = createStyledButton("Profil");

        navPanel.add(discussionsButton);
        navPanel.add(groupsButton);
        navPanel.add(profileButton);

        bottomPanel.add(inputPanel, BorderLayout.NORTH);
        bottomPanel.add(navPanel, BorderLayout.SOUTH);

        // Ajouter les composants à la fenêtre
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(contactsPanel, BorderLayout.WEST);
        frame.add(chatPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Poppins", Font.BOLD, 16));
        button.setBackground(new Color(212, 165, 165)); // Rose poudré
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 60));
        return button;
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            chatArea.append("Moi: " + message + "\n");
            messageField.setText("");
        }
    }
}



