package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class ChatGUI extends JFrame {

    private JTextField nickField, roomField, sendToField, messageField;
    private JButton connectBtn, roomBtn, listUsersBtn, uploadBtn, sendBtn, downloadBtn;
    private JTextArea chatArea;
    private JList<String> fileList; // Lista widocznych plików do pobrania
    private DefaultListModel<String> fileListModel;

    private boolean isConnected = false;
    private boolean isInRoom = false;

    private ChatApp chatApp;

    public ChatGUI() {
        setTitle("Multicast Room Chat");
        setSize(850, 550); // Zwiększone, by pomieścić listę plików
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- LINIA 1: Nick, Pokój i Połączenie ---
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Nick:"), gbc);
        nickField = new JTextField(8);
        gbc.gridx = 1;
        mainPanel.add(nickField, gbc);

        gbc.gridx = 2;
        mainPanel.add(new JLabel("Pokój:"), gbc);
        roomField = new JTextField(8);
        gbc.gridx = 3;
        mainPanel.add(roomField, gbc);

        connectBtn = new JButton("Połącz");
        connectBtn.setBackground(Color.GREEN);
        gbc.gridx = 4;
        mainPanel.add(connectBtn, gbc);

        roomBtn = new JButton("Join");
        roomBtn.setEnabled(false);
        gbc.gridx = 5;
        mainPanel.add(roomBtn, gbc);

        // --- LINIA 2: Adresowanie i Lista Użytkowników ---
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("SendTo:"), gbc);
        sendToField = new JTextField("all");
        gbc.gridx = 1; gbc.gridwidth = 3;
        mainPanel.add(sendToField, gbc);

        listUsersBtn = new JButton("List of Users");
        gbc.gridx = 4; gbc.gridwidth = 2;
        mainPanel.add(listUsersBtn, gbc);

        // --- LINIA 3: Chat Area (Lewa) i File List (Prawa) ---
        chatArea = new JTextArea(15, 30);
        chatArea.setEditable(false);
        chatArea.setBorder(BorderFactory.createTitledBorder("Rozmowa"));
        JScrollPane chatScroll = new JScrollPane(chatArea);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.weightx = 0.7;
        mainPanel.add(chatScroll, gbc);

        // Panel plików
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane fileScroll = new JScrollPane(fileList);
        fileScroll.setBorder(BorderFactory.createTitledBorder("Dostępne pliki"));
        gbc.gridx = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 0.3;
        mainPanel.add(fileScroll, gbc);

        // --- LINIA 4: Wysyłanie Wiadomości i Pobieranie ---
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.weightx = 0;
        uploadBtn = new JButton("📁 Wyślij Plik");
        mainPanel.add(uploadBtn, gbc);

        messageField = new JTextField();
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        mainPanel.add(messageField, gbc);

        sendBtn = new JButton("Wyślij");
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        mainPanel.add(sendBtn, gbc);

        downloadBtn = new JButton("⬇ Pobierz");
        downloadBtn.setEnabled(false); // Wyłączony do czasu otrzymania pliku
        gbc.gridx = 5;
        mainPanel.add(downloadBtn, gbc);

        add(mainPanel);

        chatApp = new ChatApp(this);

        // --- Eventy ---
        connectBtn.addActionListener(e -> handleConnect());
        roomBtn.addActionListener(e -> handleRoomAction());
        uploadBtn.addActionListener(e -> handleFileUpload());
        sendBtn.addActionListener(e -> handleSendMessage());
        downloadBtn.addActionListener(e -> handleFileDownload());

        // Logika aktywacji przycisku pobierania po kliknięciu na listę
        fileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                downloadBtn.setEnabled(fileList.getSelectedIndex() != -1);
            }
        });
    }

    // Symulacja: Metoda wywoływana, gdy ChatApp odbierze info o nowym pliku
    public void onFileNotificationReceived(String fileName) {
        fileListModel.addElement(fileName);
        chatArea.append("System: Użytkownik przesyła plik: " + fileName + ". Możesz go pobrać z listy.\n");
    }

    private void handleFileDownload() {
        int selectedIndex = fileList.getSelectedIndex();
        if (selectedIndex != -1) {
            String fileName = fileList.getSelectedValue();

            JFileChooser saveChooser = new JFileChooser();
            saveChooser.setSelectedFile(new File(fileName));
            int result = saveChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File destination = saveChooser.getSelectedFile();
                //TODO: Download file
                chatArea.append("System: Pobieranie " + fileName + " do " + destination.getAbsolutePath() + "\n");

                fileListModel.remove(selectedIndex);
                downloadBtn.setEnabled(false);
            }
        }
    }

    // --- Reszta metod bez zmian w logice, tylko update UI ---
    private void handleConnect() {
        if (!isConnected) {
            try {
                chatApp.setUserNickname(nickField.getText());
                chatApp.connectToGeneral();
                isConnected = true;
                updateConnectionUI();

                //TODO: RM later
                //
                // Symulacja otrzymania pliku po połączeniu (do testów UI)
                onFileNotificationReceived("dokumentacja_projektu.pdf");

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
                //TODO: imlement room join logic
            }catch (Exception e){
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
            isInRoom = true;
            roomBtn.setText("Leave");
            roomField.setEditable(false);
        } else {
            isInRoom = false;
            roomBtn.setText("Join");
            roomField.setEditable(true);
        }
    }

    private void updateConnectionUI() {
        connectBtn.setText(isConnected ? "Disconnect" : "Connect");
        connectBtn.setBackground(isConnected ? Color.RED : Color.GREEN);
        nickField.setEnabled(!isConnected);
        roomBtn.setEnabled(isConnected);
    }

    private void handleFileUpload() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chatArea.append("System: Sending File: " + fc.getSelectedFile().getName() + "\n");
        }
    }

    public void handleSendMessage() {
        if (!messageField.getText().isEmpty()) {
            try{
                chatApp.send(messageField.getText(), CommandType.MESSAGE);
            }catch (Exception e){
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
            messageField.setText("");
        }
    }

    public void addToLog(String message) {
        chatArea.append(message + "\n");
    }

    private void handleListUsers() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatGUI().setVisible(true));
    }
}