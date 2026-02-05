package org.example;

import org.json.JSONObject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MessageHandler {

    protected ChatApp chatApp;
    public MessageHandler(ChatApp chatApp) {
        this.chatApp = chatApp;
    }

    public static class Receiver extends MessageHandler {

        public Receiver(ChatApp chatApp) {
            super(chatApp);
        }

        public String  handleReceivedMessage(String payload) {
            Message message = Message.fromJson(payload);
            switch (message.getCommandType()){
                case JOIN:
                    return handleJoin(message);
                case MESSAGE:
                    return handleMessage(message);
                default:
                    return null;
            }
        }
        public String handleJoin(Message message) {
            return "[" + message.getTimestamp() + "] " + message.getNickName() + " joined room: " + message.getRoomName();
        }
        public void handleLeave(){

        }
        public void handleConnect(){

        }
        public void handleDisconnect(){

        }
        public void handleWhois(){

        }
        public void handleSendTo(){

        }
        public void handleNickBusy(){

        }
        public String handleMessage(Message message) {
            return "[" + message.getTimestamp() + "] " + message.getNickName() + ": " + message.getMessage();
        }
        public void handleWriting(){

        }
    }


    public static class Sender extends MessageHandler {
        public Sender(ChatApp chatApp) {
            super(chatApp);
        }
        public String handleMessage(String payload, CommandType commandType) {
            switch (commandType) {
                case JOIN:
                    payload = handleJoin();
                    break;
                case MESSAGE:
                    payload = handleMessage(payload);
                    break;
            }
            return payload;
        }
        public String handleJoin() {
            Message message = new Message(CommandType.JOIN, chatApp.roomHandler.curentRoom.getRoomName(), chatApp.getUserNickname(), "Hello from: " + chatApp.getUserNickname());
            return message.toJson();
        }
        public void handleLeave(){

        }
        public void handleConnect(){

        }
        public void handleDisconnect(){

        }
        public void handleWhois(){

        }
        public void handleSendTo(){

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
