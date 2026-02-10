package org.example;

import java.util.ArrayList;
import java.util.List;

public class RoomHandler {
    public List<Room> rooms;
    private String generalGroupAddress = "224.0.0.3";
    private int roomPort = 7;

    public Room curentRoom = null;

    public RoomHandler() {
        rooms = new ArrayList<Room>();
        rooms.add(new Room("General", generalGroupAddress, roomPort));
    }

    private String getGroupAddress() {
        return rooms.getLast().getRoomGroupAddress();
    }

    public Room getGeneralRoom(){
        return rooms.getFirst();
    }

    public void joinToRoom(String roomName){
        if (curentRoom.getRoomName().equals(roomName)){
            return;
        }

        Room existing = rooms.stream()
                .filter(r -> r.getRoomName().equals(roomName))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            curentRoom = existing;
        } else {
            curentRoom = new Room(roomName, generalGroupAddress, roomPort);
            rooms.add(curentRoom);
        }
    }

    public void leaveRoom(){
        rooms.remove(curentRoom);
        curentRoom = getGeneralRoom();
    }

}
