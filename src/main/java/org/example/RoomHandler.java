package org.example;

import java.util.ArrayList;
import java.util.List;

public class RoomHandler {
    public List<Room> rooms;
    private String generalGroupAddress = "224.0.0.1";
    private int roomPort =7;

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
        curentRoom = new Room(roomName, getGroupAddress(), roomPort);
        rooms.add(curentRoom);
    }

    public void leaveRoom(){
        rooms.remove(curentRoom);
        curentRoom = getGeneralRoom();
    }

    public boolean isInRoom(String roomName){
        if(roomName.equals(curentRoom.getRoomGroupAddress())){
            return true;
        }else{
            return false;
        }
    }

}
