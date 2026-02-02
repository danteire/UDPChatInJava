package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ChatGUI extends JFrame {

    private JTextField nickField, roomField, sendToField, messageField;
    private JButton connectBtn, listUsersBtn, uploadBtn, sendBtn;
    private JTextArea chatArea;
    private boolean isConnected = false;

    private ChatApp chatApp;

    public ChatGUI() {

        setTitle("Multicast Room Chat");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Nick:"), gbc);
        nickField = new JTextField(10);
        gbc.gridx = 1;
        mainPanel.add(nickField, gbc);

        gbc.gridx = 2;
        mainPanel.add(new JLabel("Pokój:"), gbc);
        roomField = new JTextField(10);
        gbc.gridx = 3;
        mainPanel.add(roomField, gbc);

        connectBtn = new JButton("Połącz");
        connectBtn.setBackground(Color.GREEN);
        gbc.gridx = 4;
        mainPanel.add(connectBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("SendTo:"), gbc);
        sendToField = new JTextField("all");
        gbc.gridx = 1; gbc.gridwidth = 2;
        mainPanel.add(sendToField, gbc);

        listUsersBtn = new JButton("List of Users");
        gbc.gridx = 3; gbc.gridwidth = 2;
        mainPanel.add(listUsersBtn, gbc);

        chatArea = new JTextArea(15, 40);
        chatArea.setEditable(false);
        chatArea.setBorder(BorderFactory.createTitledBorder("Rozmowa"));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        mainPanel.add(scrollPane, gbc);

        gbc.weighty = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        uploadBtn = new JButton("📁 Plik");
        mainPanel.add(uploadBtn, gbc);

        messageField = new JTextField();
        gbc.gridx = 1; gbc.gridwidth = 3;
        mainPanel.add(messageField, gbc);

        sendBtn = new JButton("Wyślij");
        gbc.gridx = 4; gbc.gridwidth = 1;
        mainPanel.add(sendBtn, gbc);

        add(mainPanel);

        chatApp = new ChatApp(this);

        connectBtn.addActionListener(e -> handleConnect());
        uploadBtn.addActionListener(e -> handleFileUpload());
        sendBtn.addActionListener(e -> handleSendMessage());
        listUsersBtn.addActionListener(e -> handleListUsers());
    }

    private void toggleConnection() {
        if (!isConnected) {
            isConnected = true;
            connectBtn.setText("Rozłącz");
            connectBtn.setBackground(Color.RED);
            chatArea.append("System: Połączono z pokojem " + roomField.getText() + "\n");
            lockFields(false);
        } else {
            isConnected = false;
            connectBtn.setText("Połącz");
            connectBtn.setBackground(Color.GREEN);
            chatArea.append("System: Rozłączono.\n");
            lockFields(true);
        }
    }
    private void handleConnect() {
        try{
            chatApp.connect();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            toggleConnection();
        }
    }
    private void handleFileUpload() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            chatArea.append("System: Wybrano plik do wysłania: " + selectedFile.getName() + "\n");
        }
    }

    private void handleSendMessage() {
        String msg = messageField.getText();
        if (!msg.isEmpty()) {
            chatArea.append("[" + nickField.getText() + " -> " + sendToField.getText() + "]: " + msg + "\n");
            messageField.setText("");
        }
    }

    private void handleListUsers() {
        //TODO:
    }

    private void lockFields(boolean enabled) {
        nickField.setEnabled(enabled);
        roomField.setEnabled(enabled);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatGUI().setVisible(true);
        });
    }
}