package client.controller;

import client.model.LoginModel;
import client.view.ExitOnCloseAdapter;
import client.view.LoginView;
import common.ChatRoomModel;
import common.UserModel;


import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginController {
    final int PORT = 13683;
    private final LoginView loginView;
    Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    private RegisterController register;
    private UserModel user;
    private LoginModel loginModel;

    public LoginController() {
        loginView = new LoginView();
        try {
            socket = new Socket("localhost", 2022);
            socket.setTcpNoDelay(true);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LoginController(Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream) {
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        loginView = new LoginView();
    }

    public void run() {
        loginModel = new LoginModel(inputStream, outputStream);

        loginView.loginButton.addActionListener(e -> {
            try {
                outputStream.writeObject("login");
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NullPointerException nu) {
                JOptionPane.showMessageDialog(loginView, "Oops! The server is offline. \n Please try again later", "Error", JOptionPane.ERROR_MESSAGE);
            }
            String username = loginView.usernameTextField.getText();
            String password = loginView.passwordTextField.getText();

            if (loginModel.isUser(username, password)) {
                System.out.println("Logged in!");
                loginView.dispose();
                try {
                    UserModel userModel = loginModel.getUserModel();
                        ClientController clientController = new ClientController(socket, inputStream,
                                outputStream, userModel, (ChatRoomModel) inputStream.readObject());
                        clientController.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("Failed to log in.");
            }
        });

        loginView.registerButton.addActionListener(e -> {
            register = new RegisterController(inputStream, outputStream);
        });

        loginView.setWindowAdapter(new ExitOnCloseAdapter(socket));
        loginView.setVisible(true);
    }

    public UserModel getUser() {
        return user;
    }
}
