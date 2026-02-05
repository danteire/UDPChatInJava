package org.example;

import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private CommandType commandType;
    private String roomName;
    private String nickName;
    private String message;
    private long timestamp;

    public Message(CommandType commandType, String roomName, String nickName, String message) {
        this.commandType = commandType;
        this.roomName = roomName;
        this.nickName = nickName;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public CommandType getCommandType() {return commandType;}
    public String getRoomName() {return roomName;}
    public String getNickName() {return nickName;}
    public String getMessage() {return message;}
    public long getTimestamp() {return timestamp;}

    public String toJson() {
        JSONObject json = new JSONObject();

        if (commandType != null) json.put("commandType", commandType.toString());
        json.put("nickName", nickName != null ? nickName : JSONObject.NULL);
        json.put("roomName", roomName != null ? roomName : JSONObject.NULL);
        json.put("message", message != null ? message : JSONObject.NULL);
        json.put("timestamp", timestamp);

        return json.toString();
    }

    public static Message fromJson(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        Message msg = new Message(null,null,null,null);

        if (json.has("commandType")) {
            msg.setCommandType(CommandType.valueOf(json.getString("commandType")));
        }

        msg.setNickName(json.optString("nickName", "Unknown"));
        msg.setRoomName(json.optString("roomName", "General"));
        msg.setMessage(json.optString("message", ""));
        msg.setTimestamp(json.optLong("timestamp", 0));

        return msg;
    }
}
