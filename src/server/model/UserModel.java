package server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserModel implements Serializable {
    String username;
    String password;
    String status;
    String userType;
    List<UserModel> contacts;
    List<UserModel> bookmarks;
    List<ChatRoomModel> chatRooms;
    List<MessageModel> unreadMessages;

    public UserModel(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = "";
        this.userType = "";
        this.contacts = new ArrayList<>();
        this.chatRooms = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<UserModel> getContacts() {
        return contacts;
    }

    public void setContacts(List<UserModel> contacts) {
        this.contacts = contacts;
    }

    public List<UserModel> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<UserModel> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public List<ChatRoomModel> getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(List<ChatRoomModel> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public List<MessageModel> getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(List<MessageModel> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    //sorts the names of the given list
    public void sortNames(List<UserModel> nameList) {
        nameList.sort(Comparator.comparing(UserModel::getUsername));
    }

    //returns the user if he/she is in the contact list
    public UserModel searchUser(UserModel user) {
        if(contacts.contains(user))
            return user;
        else
            return null;
    }

    public String toString() {
        return "server.model.UserModel{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", userType='" + userType + '\'' +
                ", contacts=" + contacts +
                ", bookmarks=" + bookmarks +
                ", chatRooms=" + chatRooms +
                ", unreadMessages=" + unreadMessages +
                '}';
    }
}
