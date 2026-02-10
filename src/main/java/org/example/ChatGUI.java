package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ChatGUI extends JFrame {

    private JTextField nickField, roomField, sendToField, messageField;
    private JButton connectBtn, roomBtn, listUsersBtn, sendBtn;
    private JTextArea chatArea;

    public boolean isConnected = false;
    private boolean isInRoom = false;

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
        nickField = new JTextField(8);
        gbc.gridx = 1;
        mainPanel.add(nickField, gbc);

        gbc.gridx = 2;
        mainPanel.add(new JLabel("Room:"), gbc);
        roomField = new JTextField("General", 8);
        gbc.gridx = 3;
        mainPanel.add(roomField, gbc);

        connectBtn = new JButton("Connect");
        connectBtn.setBackground(Color.GREEN);
        gbc.gridx = 4;
        mainPanel.add(connectBtn, gbc);

        roomBtn = new JButton("Join");
        roomBtn.setEnabled(false);
        gbc.gridx = 5;
        mainPanel.add(roomBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("SendTo:"), gbc);
        sendToField = new JTextField("all");
        gbc.gridx = 1; gbc.gridwidth = 3;
        mainPanel.add(sendToField, gbc);

        listUsersBtn = new JButton("List of Users");
        gbc.gridx = 4; gbc.gridwidth = 2;
        mainPanel.add(listUsersBtn, gbc);

        chatArea = new JTextArea(15, 40);
        chatArea.setEditable(false);
        chatArea.setBorder(BorderFactory.createTitledBorder("Chat"));
        JScrollPane chatScroll = new JScrollPane(chatArea);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        mainPanel.add(chatScroll, gbc);

        gbc.weighty = 0;
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Message:"), gbc);

        messageField = new JTextField();
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        mainPanel.add(messageField, gbc);

        sendBtn = new JButton("Send");
        gbc.gridx = 5;
        gbc.gridwidth = 1;
        mainPanel.add(sendBtn, gbc);

        add(mainPanel);

        chatApp = new ChatApp(this);

        connectBtn.addActionListener(e -> handleConnect());
        listUsersBtn.addActionListener(e -> handleListUsers());
        roomBtn.addActionListener(e -> handleRoomAction());
        sendBtn.addActionListener(e -> handleSendMessage());

        messageField.addActionListener(e -> handleSendMessage());
    }

    private void handleConnect() {
        if (!isConnected) {
            try {
                String nickname = nickField.getText();
                if(!validateInput(nickname)) {
                    return;
                }
                chatApp.setUserNickname(nickname);
                chatApp.connectToGeneral();
                isConnected = true;
                updateConnectionUI();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        } else {
            chatApp.disconnect();
            isConnected = false;
            updateConnectionUI();
        }
    }

    private void handleRoomAction() {
        if (!isInRoom) {
            try{
                String roomName = roomField.getText();
                if(!validateInput(roomName)) {
                    return;
                }
                chatApp.connectToRoom(roomName);
            }catch (Exception e){
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
            isInRoom = true;
            roomBtn.setText("Leave");
            roomField.setEditable(false);
        } else {

            try{
                chatApp.leaveRoom();
            }catch (Exception e){
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }

            roomField.setText("General");
            isInRoom = false;
            roomBtn.setText("Join");
            roomField.setEditable(true);
        }
    }

    public void updateConnectionUI() {
        connectBtn.setText(isConnected ? "Disconnect" : "Connect");
        connectBtn.setBackground(isConnected ? Color.RED : Color.GREEN);
        nickField.setEnabled(!isConnected);
        roomBtn.setEnabled(isConnected);
    }

    public void handleSendMessage() {
        if (!messageField.getText().isEmpty()) {
            if(!sendToField.getText().isEmpty() && !(Objects.equals(sendToField.getText(), "all"))){
                try{
                    chatApp.sendTo(messageField.getText(), sendToField.getText());
                }catch (Exception e){
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }else{
                try{
                    chatApp.send(messageField.getText(), CommandType.MESSAGE);
                }catch (Exception e){
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }
            messageField.setText("");
        }
    }

    public void addToLog(String message) {
        chatArea.append(message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private boolean validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Input cannot be empty!", "Błąd walidacji", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (input.length() < 3 || input.length() > 12) {
            JOptionPane.showMessageDialog(this, "Input must be 3-12 characters long!", "Validate Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (input.contains(" ")) {
            JOptionPane.showMessageDialog(this, "Input cannot have whitespace characters!", "Validate Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!input.matches("^[a-zA-Z0-9]+$")) {
            JOptionPane.showMessageDialog(this, "Input can have only alphanumeric characters!", "Validate Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void handleListUsers() {
        addToLog("List of Active Users:");
        try {
            chatApp.send(null, CommandType.WHOIS);
        }catch (Exception e){
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatGUI().setVisible(true));
    }
}