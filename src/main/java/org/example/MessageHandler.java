package org.example;

import org.json.JSONObject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MessageHandler {

    private void commandHadler(CommandType command){
        switch(command){
            case JOIN:
                break;
            case LEAVE:
                break;
            case CONNECT:
                break;
            case DISCONNECT:
                break;
            case WHOIS:
                break;
            case SENDTO:
                break;
            case NICK_BUSY:
                break;
            default:
                break;
        }
    }
    private String messageMapper(String message, String command) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("command", command);
        jsonObject.put("room", roomHandler.curentRoom.getRoomName());
        jsonObject.put("nick", getUserNickname());
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timeString = now.format(formatter);

        jsonObject.put("time", timeString);
        jsonObject.put("message", message);

        return jsonObject.toString();
    }

    private String messageUnwrapper(String msg) {
        JSONObject unwrapMessage = new JSONObject(msg);
        String command = unwrapMessage.getString("command");
        String roomName = unwrapMessage.getString("room");
        String nick = unwrapMessage.getString("nick");
        String time = unwrapMessage.getString("time");
        String message = unwrapMessage.getString("message");

    }
}
