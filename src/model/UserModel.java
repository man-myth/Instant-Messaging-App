package model;

import java.io.Serializable;
import java.util.ArrayList;
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

    public void sortNames(List<UserModel> nameList) {

    }

    public UserModel searchUser(UserModel user) {
        return null;
    }
}
