package model;

import java.util.List;

public class ChatRoomModel {
    String name;
    List<UserModel> users;
    List<MessageModel> chatHistory;

    public UserModel searchUser(UserModel user) {
        return null;
    }

    public void addUser(UserModel user) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }

    public List<MessageModel> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<MessageModel> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public void kickUser(UserModel user) {

    }

    /**
     * Default Constructor
     * @param name of the chat room
     */
    public ChatRoomModel(String name) {
        this.name = name;
    }

}
