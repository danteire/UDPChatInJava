package org.example;

import java.util.ArrayList;
import java.util.List;

public class RoomHandler {
    public List<Room> rooms;
    private String generalGroupAddress = "224.0.0.1";
    private int roomPort =7;

    public RoomHandler() {
        rooms = new ArrayList<Room>();
        rooms.add(new Room("General", generalGroupAddress, roomPort));
    }

    private String getLastGroupAddress() {
        return rooms.getLast().getRoomGroupAddress();
    }

    public Room getGeneralRoom(){
        return rooms.getFirst();
    }

    public Room addRoom(String roomName){
        Room room = new Room(roomName,roomPort);
        room.setRoomGroupAddress(room.incramentRoomAddress(getLastGroupAddress()));
        rooms.add(room);
        return room;
    }

    public boolean containsRoom(final String name){
        return rooms.stream().anyMatch(o -> o
                .getRoomName()
                .equals(name));
    }

    public void joinToRoom(String roomName){
        if(!containsRoom(roomName)){
            Room room = addRoom(roomName);
        }

        //TODO: leave curremt room
        //TODO: connect to a new room, set as current
    }

    public void leaveRoom(Room room){
        //TODO: disconnect from a current room
        rooms.remove(room);
    }

}
