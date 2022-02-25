package client.controller;

import client.model.LoginModel;
import client.view.ExitOnCloseAdapter;
import client.view.LoginView;
import server.controller.AdminController;
import common.ChatRoomModel;
import common.UserModel;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class LoginController {
    private RegisterController register;
    private UserModel user;
    private final LoginView loginView;
    private LoginModel loginModel;
    Socket socket;
    final int PORT = 2022;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    public LoginController() {
        loginView = new LoginView();
        try {
            socket = new Socket("localhost", PORT);
            socket.setTcpNoDelay(true);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e){
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
            } catch (NullPointerException nu){
                JOptionPane.showMessageDialog(loginView, "Oops! The server is offline. \n Please try again later", "Error", JOptionPane.ERROR_MESSAGE);
            }
            String username = loginView.usernameTextField.getText();
            String password = loginView.passwordTextField.getText();

            if (loginModel.isUser(username, password)) {
                System.out.println("Logged in!");
                loginView.dispose();
                try {
                    UserModel userModel = loginModel.getUserModel();
                    if (userModel.getUsername().equals("admin"))
                        new AdminController(socket, inputStream, outputStream,
                                userModel, (ChatRoomModel) inputStream.readObject()).run();
                    else{
                        ClientController clientController = new ClientController(socket, inputStream,
                                outputStream, userModel, (ChatRoomModel) inputStream.readObject());
                        clientController.run();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("Failed to log in.");
                //loginView.dispose();
                //LoginController login = new LoginController();
                //login.run();
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
