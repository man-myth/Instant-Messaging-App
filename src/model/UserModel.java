package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserModel implements Serializable {
    String username;
    String password;
    String status;
    String userType;
    Boolean isAdmin;
    List<UserModel> contacts;
    List<UserModel> bookmarks;
    List<ChatRoomModel> chatRooms;
    List<MessageModel> unreadMessages;

    public UserModel(String username, String password, Boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.status = "";
        this.userType = "";
        this.contacts = new ArrayList<>();
        this.chatRooms = new ArrayList<>();
        this.isAdmin = isAdmin;
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

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
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

    public void sortNames(List<UserModel> nameList) {

    }

    public UserModel searchUser(UserModel user) {
        return null;
    }

    public String toString() {
        return "UserModel{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", userType='" + userType + '\'' +
                ", isAdmin=" + isAdmin +
                ", contacts=" + contacts +
                ", bookmarks=" + bookmarks +
                ", chatRooms=" + chatRooms +
                ", unreadMessages=" + unreadMessages +
                '}';
    }
}
