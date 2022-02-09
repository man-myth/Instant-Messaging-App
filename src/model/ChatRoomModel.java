package model;

import java.util.List;

public class ChatRoomModel {
    private String name;
    private List<UserModel> users;
    private List<MessageModel> chatHistory;

    private UserModel searchUser(UserModel user) {
        //returns the user if userList contains the specified user
        if(users.contains(user))
            return user;
        else
            return null;
    }

    private void addUser(UserModel user) {
        users.add(user); //adds user to the list of users
    }

    private void kickUser(UserModel user) {
        users.remove(user); //removes the user from list of users
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



    /**
     * Default Constructor
     * @param name of the chat room
     */
    public ChatRoomModel(String name) {
        this.name = name;
    }

}
