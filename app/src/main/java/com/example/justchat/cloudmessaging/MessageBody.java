package com.example.justchat.cloudmessaging;

public class MessageBody {

    private String to;
    private Data data;

    public MessageBody(String to, Data data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
