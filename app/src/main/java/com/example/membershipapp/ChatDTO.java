package com.example.membershipapp;

public class ChatDTO {
    private String chatUserName;
    private String chatMessage;
    private String chatDate;

    public ChatDTO(String chatUserName, String chatMessage, String chatDate) {
        this.chatUserName = chatUserName;
        this.chatMessage = chatMessage;
        this.chatDate = chatDate;
    }

    public String getChatUserName() {
        return chatUserName;
    }

    public void setChatUserName(String chatUserName) {
        this.chatUserName = chatUserName;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public String getChatDate() {
        return chatDate;
    }

    public void setChatDate(String chatDate) {
        this.chatDate = chatDate;
    }
}
