package client.controller;

import server.model.UserModel;
import server.model.ChatRoomModel;
import server.model.MessageModel;

import javax.swing.text.View;
import java.util.List;

public class UserController {
    private UserModel model;
    private View view;

    public UserController(UserModel model, View view){
        this.model = model;
        this.view = view;
    }
}
