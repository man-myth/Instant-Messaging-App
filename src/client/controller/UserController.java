package client.controller;

import common.UserModel;

import javax.swing.text.View;

public class UserController {
    private final UserModel model;
    private final View view;

    public UserController(UserModel model, View view) {
        this.model = model;
        this.view = view;
    }
}
