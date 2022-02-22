package server.model;

import java.io.Serializable;
import java.util.*;

public class UserModel implements Serializable {
    private String username;
    private String password;
    private String status;
    private Boolean isActive;
    private List<UserModel> contacts;
    private List<ChatRoomModel> bookmarks;
    private List<ChatRoomModel> chatRooms;
    private List<MessageModel> unreadMessages;

    public UserModel(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = "Online";
        this.contacts = new ArrayList<>();
        this.chatRooms = new ArrayList<>();
        this.bookmarks = new ArrayList<>();
        this.isActive = false;
    }

    public UserModel() {

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

    public List<UserModel> getContacts() {
        return contacts;
    }

    public void setContacts(List<UserModel> contacts) {
        this.contacts = contacts;
    }

    public List<ChatRoomModel> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<ChatRoomModel> bookmarks) {
        this.bookmarks = bookmarks;
    }
    public List<ChatRoomModel> getChatRooms() {
        return chatRooms;
    }

    public List<ChatRoomModel> getRoomsList(){
        List<ChatRoomModel> rooms = new ArrayList<>();
        //put bookmarked rooms at the top list
        try {
            for (ChatRoomModel m : getBookmarks()) {
                rooms.add(m);
            }
            for (ChatRoomModel r : this.getChatRooms()) {
                if (!rooms.contains(r)) {
                    rooms.add(r);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return rooms;
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

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    //sorts the names of the given list
    public void sortNames(List<UserModel> nameList) {
        nameList.sort(Comparator.comparing(UserModel::getUsername));
    }

    //returns the user if he/she is in the contact list

    public UserModel searchUserInContact(String username) {
        for(UserModel u: contacts){
            if (u.getUsername().equals(username))
                return u;
        }
        return null;
    }

    public void updateChatroom(String roomName, ChatRoomModel newChatRoom) {
        for (int i = 0; i < chatRooms.size(); i++) {
            if (chatRooms.get(i).getName().equals(roomName)) {
                chatRooms.set(i, newChatRoom);
            }
        }
    }

    public boolean hasContact(String username) {
        for (UserModel user : contacts) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "server.model.UserModel{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", bookmarks=" + bookmarks +
                ", chatRooms=" + chatRooms +
                ", unreadMessages=" + unreadMessages +
                '}';
    }

}
