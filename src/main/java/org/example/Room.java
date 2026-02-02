package org.example;

import java.util.Arrays;

public class Room {
    private final String roomName;
    private String roomGroupAddress;
    private final int roomPort;

    public Room(String roomName, String roomGroupAddress, int roomPort) {
        this.roomName = roomName;
        this.roomGroupAddress = roomGroupAddress;
        this.roomPort = roomPort;
    }

    public Room(String roomName, int roomPort) {
        this.roomName = roomName;
        this.roomGroupAddress = null;
        this.roomPort = roomPort;
    }

    public String incramentRoomAddress(String roomGroupAddress) {

        System.out.println(roomGroupAddress);

        String[] address = roomGroupAddress.split("\\.");
        String returnAddress = null;

        System.out.println(Arrays.toString(address));

        Integer incAdrress = (Integer.valueOf(address[3]));

        if (incAdrress + 1 >= 255) {
            for(int i = 0; i < 3; i++){
                returnAddress.concat(address[i]);
                returnAddress.concat(".");
            }
            incAdrress += 1;
            returnAddress.concat((incAdrress).toString());
        }else{
            incAdrress = (Integer.valueOf(address[2]));
            for(int i = 0; i < 2; i++){
                returnAddress.concat(address[i]);
                returnAddress.concat(".");
            }
            incAdrress += 1;
            returnAddress.concat((incAdrress).toString());
            returnAddress.concat(".0");
        }

        System.out.println(returnAddress);

        return returnAddress;
    }

    public void setRoomGroupAddress(String roomGroupAddress) {
        this.roomGroupAddress = roomGroupAddress;
    }
    public String getRoomGroupAddress() {
        return roomGroupAddress;
    }
    public String getRoomName() {
        return roomName;
    }
    public Integer getRoomPort() {
        return roomPort;
    }
}
