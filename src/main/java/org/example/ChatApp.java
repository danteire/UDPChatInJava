package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.List;

public class ChatApp {

    private final ChatGUI chatGUI;
    private RoomHandler roomHandler = new RoomHandler();
    public Room generalRoom = roomHandler.getGeneralRoom();
    private Room curentRoom = null;


    protected MulticastSocket socket = null;
    private InetSocketAddress groupAddress = null;
    protected byte[] buf = new byte[256];



    public ChatApp(ChatGUI chatGUI) {
        this.chatGUI = chatGUI;
    }

    public void connectToGeneral() {
        try {
            curentRoom = generalRoom;

            socket = new MulticastSocket(curentRoom.getRoomPort());
            InetAddress inetAddress = InetAddress.getByName(curentRoom.getRoomGroupAddress());
            groupAddress = new InetSocketAddress(inetAddress, curentRoom.getRoomPort());
            socket.joinGroup(groupAddress, null);

            Thread daemon = new Thread(this::receive);
            daemon.setDaemon(true);
            daemon.start();

            //TODO: PASS TO GUI
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Room> getRooms() {
        return roomHandler.rooms;
    }

    public void connectToRoom(String roomName) {
        roomHandler.joinToRoom(roomName);
    }

    public void disconnect() {
        try {
            if (socket != null && groupAddress != null) {
                socket.leaveGroup(groupAddress, null);
                socket.close();
                //TODO: PASS TO GUI
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void receive() {
        try {
            while (!socket.isClosed()) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                //TODO: PASS TO GUI
                if ("end".equals(received.trim())) {
                    break;
                }
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                //TODO: PASS TO GUI
            }
        }
    }
}
