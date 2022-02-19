package client.controller;

import client.model.LoginModel;
import client.view.ExitOnCloseAdapter;
import client.view.LoginView;
import server.model.ChatRoomModel;
import server.model.UserModel;

import java.io.*;
import java.net.Socket;

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
        loginView = new LoginView();
        //log in
        //if the users clicked the register button, open the registerController
        // then show the Login GUI again
        // validate the user
    }

    public void run() {
        try {
            socket = new Socket("localhost", PORT);
            socket.setTcpNoDelay(true);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());

            loginModel = new LoginModel(inputStream, outputStream);

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
                    try {
                        loginView.dispose();
                        ClientController clientController = new ClientController(socket, inputStream, outputStream, (UserModel) inputStream.readObject(), (ChatRoomModel) inputStream.readObject());
                        clientController.run();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserModel getUser() {
        return user;
    }
}
