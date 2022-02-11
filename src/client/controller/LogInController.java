package client.controller;

import client.view.LoginView;
import common.UserModel;

public class LogInController {
    private RegisterController register;
    private UserModel user;
    private LoginView loginView;

    public LogInController() {
        loginView = new LoginView();
        //log in
        //if the users clicked the register button, open the registerController
        // then show the Login GUI again
        // validate the user

    }

    public UserModel getUser() {
        return user;
    }
}
