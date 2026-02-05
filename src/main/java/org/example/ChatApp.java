package org.example;

import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private MessageHandler.Sender sender;
    private MessageHandler.Receiver receiver;


    public ChatApp(ChatGUI chatGUI) {
        this.chatGUI = chatGUI;
        this.sender = new MessageHandler.Sender(this);
        this.receiver = new MessageHandler.Receiver(this);
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
    public String getUserNickname() {
        return userNickname;
    }


    public void connectToGeneral() {
        try {
            roomHandler.curentRoom = generalRoom;

            socket = new MulticastSocket(roomHandler.curentRoom.getRoomPort());
            InetAddress inetAddress = InetAddress.getByName(roomHandler.curentRoom.getRoomGroupAddress());
            groupAddress = new InetSocketAddress(inetAddress, roomHandler.curentRoom.getRoomPort());
            socket.joinGroup(groupAddress, null);

            Thread daemon = new Thread(this::receive);
            daemon.setDaemon(true);
            daemon.start();

            send(null, CommandType.JOIN);
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
        chatGUI.addToLog("Joined room: " + roomHandler.curentRoom.getRoomName());
    }

    public void leaveRoom() {
        if((roomHandler.curentRoom == null || roomHandler.curentRoom == generalRoom) && socket != null) {
            return;
        }
        chatGUI.addToLog("Leaving room");
        roomHandler.leaveRoom();
    }

    public void disconnect() {
        try {
            if (socket != null && groupAddress != null) {
                socket.leaveGroup(groupAddress, null);
                socket.close();

                chatGUI.addToLog("Disconnected from " + groupAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void send(String message, CommandType command){
        try {
            message = sender.handleMessage(message, command);

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
                    SwingUtilities.invokeLater(() -> chatGUI.addToLog(processedMsg));
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
