package org.example;

import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class MessageHandler {

    protected ChatApp chatApp;
    public MessageHandler(ChatApp chatApp) {
        this.chatApp = chatApp;
    }

    public String getFormattedTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        return instant.atZone(ZoneId.systemDefault())
                .toLocalTime()
                .format(formatter);
    }

    public static class Receiver extends MessageHandler {

        public Receiver(ChatApp chatApp) {
            super(chatApp);
        }

        public String  handleReceivedMessage(String payload) {
            Message message = Message.fromJson(payload);
            switch (message.getCommandType()){
                case CONNECT:
                    return handleConnect(message);
                case DISCONNECT:
                    return handleDisconnect(message);
                case JOIN:
                    return handleJoin(message);
                case MESSAGE:
                    return handleMessage(message);
                case WHOIS:
                    handleWhois();
                    return null;
                case IAM:
                    return handleIam(message);
                case SENDTO:
                    return handleSendTo(message);
                default:
                    return null;
            }
        }
        public String handleJoin(Message message) {
            return "[" + getFormattedTime(message.getTimestamp()) + "] [" + message.getNickName() + "]: " + message.getMessage();
        }
        public void handleLeave(){

        }
        public String handleConnect(Message message) {
            return "[" + getFormattedTime(message.getTimestamp()) + "] [" + message.getNickName()+"]: " + message.getMessage();
        }
        public String handleDisconnect(Message message) {
            return "[" + getFormattedTime(message.getTimestamp()) + "] "+ message.getMessage();
        }
        public void handleWhois() {
            super.chatApp.send(null, CommandType.IAM);
        }
        public String handleIam(Message message) {
            return message.getMessage();
        }
        public String handleSendTo(Message message) {
            if(Objects.equals(message.getRoomName(), chatApp.getUserNickname())){
               return "[" + getFormattedTime(message.getTimestamp()) + "] [" + message.getNickName() + "] whispers: " + message.getMessage();
            }else{
                return null;
            }
        }
        public void handleNickBusy(){

        }
        public String handleMessage(Message message) {
            return "[" + getFormattedTime(message.getTimestamp()) + "] [" + message.getNickName() + "]: " + message.getMessage();
        }
        public void handleWriting(){

        }
    }


    public static class Sender extends MessageHandler {
        public Sender(ChatApp chatApp) {
            super(chatApp);
        }
        public String handleMessage(String payload, CommandType commandType, String receiver) {
            switch (commandType) {
                case CONNECT:
                    payload = handleConnect();
                    break;
                case DISCONNECT:
                    payload = handleDisconnect();
                    break;
                case JOIN:
                    payload = handleJoin();
                    break;
                case MESSAGE:
                    payload = handleMessage(payload);
                    break;
                case WHOIS:
                    payload = handleWhois();
                    break;
                case IAM:
                    payload = handleIam();
                    break;
                case SENDTO:
                    payload = handleSendTo(payload, receiver);
                    break;
            }
            return payload;
        }
        public String handleJoin() {
            Message message = new Message(CommandType.JOIN, chatApp.roomHandler.curentRoom.getRoomName(), chatApp.getUserNickname(), "User: " + chatApp.getUserNickname() + " joined room: " + chatApp.roomHandler.curentRoom.getRoomName());
            return message.toJson();
        }
        public void handleLeave(){

        }
        public String handleConnect() {
            Message message = new Message(CommandType.CONNECT, chatApp.roomHandler.curentRoom.getRoomName(), chatApp.getUserNickname(), "Hello from: " + chatApp.getUserNickname());
            return message.toJson();
        }
        public String handleDisconnect(){
            Message message = new Message(CommandType.DISCONNECT, chatApp.roomHandler.curentRoom.getRoomName(), chatApp.getUserNickname(), "User: " + chatApp.getUserNickname() + " disconnected");
            return message.toJson();
        }
        public String handleWhois(){
            Message message = new Message(CommandType.WHOIS, null, chatApp.getUserNickname(),null);
            return message.toJson();
        }
        public String handleIam(){
            Message message = new Message(CommandType.IAM, null, chatApp.getUserNickname(),chatApp.getUserNickname());
            return message.toJson();
        }
        public String handleSendTo(String payload, String receiver) {
            Message message = new Message(CommandType.SENDTO, receiver, chatApp.getUserNickname(),payload);
            return message.toJson();
        }
        public void handleNickBusy(){

        }
        public String handleMessage(String payload) {
            Message message = new Message(CommandType.MESSAGE, chatApp.roomHandler.curentRoom.getRoomName(), chatApp.getUserNickname(), payload);
            return message.toJson();
        }
        public void handleWriting(){

        }
    }
}
