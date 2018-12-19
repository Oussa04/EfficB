package org.meteor.efficaisse.model;

public class Message {

    private String message;

    public Message(String body) {
        this.message = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
