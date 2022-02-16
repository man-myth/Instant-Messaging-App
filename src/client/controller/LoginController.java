package client.controller;

import client.model.LoginModel;
import client.view.ClientView;
import client.view.LoginView;
import server.model.UserModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class LoginController {
    private RegisterController register;
    private UserModel user;
    private LoginView loginView;
    private LoginModel loginModel;

    public LoginController(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        loginModel = new LoginModel(inputStream, outputStream);
        loginView = new LoginView();

        loginView.loginButton.addActionListener(e -> {
            try {
                outputStream.writeObject("login");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            String username = loginView.usernameTextField.getText();
            String password = loginView.passwordTextField.getText();

            if (loginModel.isUser(username, password)) {
                System.out.println("Logged in!");
                new ClientView().setVisible(true);
            } else {
                System.out.println("Failed to log in.");
            }
        });

        loginView.registerButton.addActionListener(e -> {
            register = new RegisterController(inputStream, outputStream);
        });
        //log in
        //if the users clicked the register button, open the registerController
        // then show the Login GUI again
        // validate the user
        loginView.setVisible(true);
    }

    public void run() {

    }

    public UserModel getUser() {
        return user;
    }
}
