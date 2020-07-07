package com.example.justchat.model;

public class Chats {

    private String senderId;
    private String receiverId;
    private String message;
    private Boolean issen;

    public Chats() {
    }

    public Chats(String senderId, String receiverId, String message, Boolean issen) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.issen = issen;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIssen() {
        return issen;
    }

    public void setIssen(Boolean issen) {
        this.issen = issen;
    }

    //     map.put("senderId",senderId);
//        map.put("receiverId",receiverId);
//        map.put("message",message);
//        map.put("issen",isseen);

}
