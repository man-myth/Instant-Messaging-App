package controller;

import model.ChatRoomModel;
import model.MessageModel;
import model.UserModel;
import javax.swing.text.View;
import java.util.List;

public class UserController {
    private UserModel model;
    private View view;

    public UserController(UserModel model, View view){
        this.model = model;
        this.view = view;
    }

    public void setUsername(String username){
        model.setUsername(username);
    }

    public String getUsername(){
        return model.getUsername();
    }

    public void setPassword(String password){
        model.setPassword(password);
    }

    public String getPassword(){
        return model.getPassword();
    }

    public void setUserType(String userType){
        model.setUserType(userType);
    }

    public String getUserType(){
        return model.getUserType();
    }

    public void setContacts(List<UserModel> contacts){
        model.setContacts(contacts);
    }

    public List<UserModel> getContacts(){
        return model.getContacts();
    }
    public void setBookmarks(List<UserModel> bookmarks){
        model.setBookmarks(bookmarks);
    }
    public List<UserModel> getBookmarks(){
        return model.getBookmarks();
    }
    public void setChatRooms(List<ChatRoomModel> chatRooms){
        model.setChatRooms(chatRooms);
    }
    public List<ChatRoomModel> getChatRooms(){
        return model.getChatRooms();
    }
    public void setUnreadMessages(List<MessageModel> unreadMessages){
        model.setUnreadMessages(unreadMessages);
    }
    public List<MessageModel> getUnreadMessages(){
        return model.getUnreadMessages();
    }
    public void updateView(){
        /*
        Method in updating the view with the content from the model to be added
         */
    }
}
