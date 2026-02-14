package org.example;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

import java.util.List;

public class ChatApp {

    private final ChatGUI chatGUI;

    private final MessageHandler messageHandler = new MessageHandler(this);
    public RoomHandler roomHandler = new RoomHandler();
    public Room generalRoom = roomHandler.getGeneralRoom();


    protected MulticastSocket socket = null;
    private InetSocketAddress groupAddress = null;

    protected byte[] buf = new byte[4096];

    public String userNickname = "";
    public boolean isConnectionVerified;

    private final MessageHandler.Sender sender;
    private final MessageHandler.Receiver receiver;
    private Timer verificationTimer;

    public ChatApp(ChatGUI chatGUI) {
        this.chatGUI = chatGUI;
        this.sender = new MessageHandler.Sender(this);
        this.receiver = new MessageHandler.Receiver(this);
        this.isConnectionVerified = false;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
    public String getUserNickname() {
        return userNickname;
    }
    public void addToLogPass(String message) {
        chatGUI.addToLog(message);
    }
    public void setConnectionVerified(boolean verified) {this.isConnectionVerified = verified;}
    public boolean isConnectionVerified() {return isConnectionVerified;}

    public void connectToGeneral() {
        try {
            this.isConnectionVerified = false;
            roomHandler.curentRoom = generalRoom;

            socket = new MulticastSocket(roomHandler.curentRoom.getRoomPort());
            InetAddress inetAddress = InetAddress.getByName(roomHandler.curentRoom.getRoomGroupAddress());
            groupAddress = new InetSocketAddress(inetAddress, roomHandler.curentRoom.getRoomPort());
            socket.joinGroup(groupAddress, null);

            Thread daemon = new Thread(this::receive);
            daemon.setDaemon(true);
            daemon.start();

            send(null, CommandType.CONNECT);

            if (verificationTimer != null && verificationTimer.isRunning()) {
                verificationTimer.stop();
            }
            verificationTimer = new Timer(1000, e -> {
                if (socket != null && !socket.isClosed() && !isConnectionVerified) {
                    isConnectionVerified = true;
                    chatGUI.addToLog("System: Nickname Verified.");
                }
            });

            verificationTimer.setRepeats(false);
            verificationTimer.start();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Room> getRooms() {
        return roomHandler.rooms;
    }

    public void connectToRoom(String roomName) {
        chatGUI.addToLog("Leaving room: " + roomHandler.curentRoom.getRoomName());
        roomHandler.joinToRoom(roomName);
        send(null, CommandType.JOIN);
    }

    public void leaveRoom() {
        if (roomHandler.curentRoom == null || roomHandler.curentRoom.getRoomName().equals("General")) {
            return;
        }
        send("Has left the room.", CommandType.LEAVE);
        roomHandler.leaveRoom();
    }

    public void disconnect() {
        if (verificationTimer != null) {
            verificationTimer.stop();
        }
        try {
            if (isConnectionVerified) {
                send(null, CommandType.DISCONNECT);
                leaveRoom();
            }
            if (socket != null && groupAddress != null) {
                socket.leaveGroup(groupAddress, null);
                socket.close();

                SwingUtilities.invokeLater(() -> chatGUI.addToLog("Disconnected from " + roomHandler.curentRoom.getRoomName()));            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setDisconnectGUI() {
        SwingUtilities.invokeLater(() -> {
            chatGUI.isConnected = false;
            chatGUI.updateConnectionUI();
        });
    }


    public void send(String message, CommandType command){
        try {
            message = sender.handleMessage(message, command, null);

            byte[] buf = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, groupAddress);
            socket.send(packet);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendTo(String message, String receiver){
        try {
            message = sender.handleSendTo(message, receiver);

            byte[] buf = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, groupAddress);
            socket.send(packet);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void receive() {
        try {
            while (!socket.isClosed()) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());

                try {
                    String processedMsg = receiver.handleReceivedMessage(received);
                    if (processedMsg != null) {
                        SwingUtilities.invokeLater(() -> chatGUI.addToLog(processedMsg));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (IOException e){
            if (!socket.isClosed()) {
                e.printStackTrace();
            }
        }
    }


}
