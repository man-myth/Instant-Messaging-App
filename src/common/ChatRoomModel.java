package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomModel implements Serializable {
    private String name;
    private List<UserModel> users;
    private List<MessageModel> chatHistory;
    private String admin;

    /**
     * Default Constructor
     *
     * @param name of the chat room
     */
    public ChatRoomModel(String name, String admin) {
        this.name = name;
        this.admin = admin;
        this.users = new ArrayList<>();
        this.chatHistory = new ArrayList<>();
    }

    public ChatRoomModel(String name, List<UserModel> users, List<MessageModel> chatHistory, String admin) {
        this.name = name;
        this.users = users;
        this.chatHistory = chatHistory;
        this.admin = admin;
    }

    // returns the user if userList contains the specified user
    public UserModel searchUser(String username) {
        for (UserModel u : users) {
            if (u.getUsername().equals(username))
                return u;
        }
        return null;
    }

    // returns the message if message contains a specified text
    public MessageModel searchMessage(String message) {
        for (MessageModel c : chatHistory) {
            if (c.getContent().equalsIgnoreCase(message))
                return c;
        }
        return null;

    }

    // checks if user is in the chat room
    public boolean isUserHere(String username) {
        for (UserModel u : users) {
            if (u.getUsername().equals(username))
                return true;
        }
        return false;
    }

    // adds user to the list of users
    public void addUser(UserModel user) {
        users.add(user);
    }

    // removes the user from list of users
    public void kickUser(UserModel user) {
        users.remove(user);
    }

    // Setters
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

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return name;
    }
}
