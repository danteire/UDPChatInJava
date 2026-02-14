package org.example;


import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
            boolean isForCurrentRoom = message.getRoomName().equals(chatApp.roomHandler.curentRoom.getRoomName());

            boolean isPrivateToMe = (message.getCommandType() == CommandType.SENDTO
                    && message.getRoomName().equals(chatApp.getUserNickname()));

            boolean isPrivateFromMe = (message.getCommandType() == CommandType.SENDTO
                    && message.getNickName().equals(chatApp.getUserNickname()));

            if (!isForCurrentRoom && !isPrivateToMe && !isPrivateFromMe) {
                return null;
            }
            switch (message.getCommandType()){
                case CONNECT:
                    return handleConnect(message);
                case NICK_BUSY:
                    return handleNickBusy(message);
                case DISCONNECT:
                    return handleDisconnect(message);
                case JOIN:
                    return handleJoin(message);
                case LEAVE:
                    return handleLeave(message);
                case MESSAGE:
                    return handleMessage(message);
                case WHOIS:
                    handleWhois(message);
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
            return "[" + getFormattedTime(message.getTimestamp()) + "] "+ "[" + message.getRoomName() + "] "  +"[" + message.getNickName() + "]: " + message.getMessage();
        }
        public String handleLeave(Message message) {
            return "[" + getFormattedTime(message.getTimestamp()) + "] "+ "[" + message.getRoomName() + "] "  +"[" + message.getNickName() + "]: " + message.getMessage();
        }
        public String handleConnect(Message message) {
            if (message.getNickName().equals(chatApp.getUserNickname())) {
                if (chatApp.isConnectionVerified()) {
                    super.chatApp.send(null, CommandType.NICK_BUSY);
                    return null;
                }
            }
            return "[" + getFormattedTime(message.getTimestamp()) + "] "+ "[" + message.getRoomName() + "] "  +"[" + message.getNickName() + "]: " + message.getMessage();
        }
        public String handleDisconnect(Message message) {
            return "[" + getFormattedTime(message.getTimestamp()) + "] "+ "[" + message.getRoomName() + "] "  +"[" + message.getNickName() + "]: "+ message.getMessage();
        }
        public void handleWhois(Message message) {
            if(message.getNickName().equals(chatApp.getUserNickname())){
            }else{
                super.chatApp.send(message.getNickName(), CommandType.IAM);
            }
        }
        public String handleIam(Message message) {
            if (message.getNickName().equals(chatApp.getUserNickname()) || !message.getMessage().equals(chatApp.getUserNickname())) {
                return null;
            }else{
                return message.getNickName();
            }
        }
        public String handleSendTo(Message message) {
            return "[" + getFormattedTime(message.getTimestamp()) + "] " + "[" + message.getNickName() + "] whispers to ["+ message.getRoomName() +"]: " + message.getMessage();
        }
        public String handleNickBusy(Message message) {
            boolean isMyNick = message.getNickName().equals(chatApp.getUserNickname());

            if (isMyNick && !chatApp.isConnectionVerified()) {
                chatApp.setDisconnectGUI();
                chatApp.disconnect();

                return "Error: Nick '" + message.getNickName() + "' is currently in use!";
            }
            return null;
        }
        public String handleMessage(Message message) {
            return "[" + getFormattedTime(message.getTimestamp()) + "] "+ "[" + message.getRoomName() + "] "  +"[" + message.getNickName() + "]: " + message.getMessage();
        }
        public void handleWriting(){}
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
                case NICK_BUSY:
                    payload = handleNickBusy();
                    break;
                case JOIN:
                    payload = handleJoin();
                    break;
                case LEAVE:
                    payload = handleLeave();
                    break;
                case MESSAGE:
                    payload = handleMessage(payload);
                    break;
                case WHOIS:
                    payload = handleWhois();
                    break;
                case IAM:
                    payload = handleIam(payload);
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
        public String handleLeave(){
            Message message = new Message(CommandType.LEAVE, chatApp.roomHandler.curentRoom.getRoomName(),chatApp.getUserNickname(), "User: " + chatApp.getUserNickname() + " left room: " + chatApp.roomHandler.curentRoom.getRoomName());
            return message.toJson();
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
        public String handleIam(String payload) {
            Message message = new Message(CommandType.IAM, null, chatApp.getUserNickname(),payload);
            return message.toJson();
        }
        public String handleSendTo(String payload, String receiver) {
            Message message = new Message(CommandType.SENDTO, receiver, chatApp.getUserNickname(),payload);
            return message.toJson();
        }
        public String handleNickBusy(){
            Message message = new Message(CommandType.NICK_BUSY, null, chatApp.getUserNickname(),null);
            return message.toJson();
        }
        public String handleMessage(String payload) {
            Message message = new Message(CommandType.MESSAGE, chatApp.roomHandler.curentRoom.getRoomName(), chatApp.getUserNickname(), payload);
            return message.toJson();
        }
        public void handleWriting(){}
    }
}
