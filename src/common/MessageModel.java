package common;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class MessageModel implements Serializable {
    private UserModel sender;
    private ChatRoomModel receiver;
    private String content;
    private LocalTime time;
    private LocalDate date;

    public MessageModel(UserModel sender, ChatRoomModel receiver, String content, LocalTime time, LocalDate date) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.time = time;
        this.date = date;
    }

    //returns true if thisMessage is equals to anotherMessage
    public boolean equals(MessageModel thisMessage, MessageModel anotherMessage) {
        return thisMessage.content.equals(anotherMessage.content);
    }

    public UserModel getSender() {
        return sender;
    }

    //-----Setters and Getters-----
    public void setSender(UserModel sender) {
        this.sender = sender;
    }

    public ChatRoomModel getReceiver() {
        return receiver;
    }

    public void setReceiver(ChatRoomModel receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
