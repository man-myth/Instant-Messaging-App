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
import java.net.Socket;
import java.util.Scanner;

public class LoginController {
    private RegisterController register;
    private UserModel user;
    private LoginView loginView;
    private LoginModel loginModel;
    Socket socket;
    final int PORT = 2022;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    public LoginController() {
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
    }

    public void run() {
        try {
            socket = new Socket("localhost", PORT);
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            loginView.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserModel getUser() {
        return user;
    }
}
