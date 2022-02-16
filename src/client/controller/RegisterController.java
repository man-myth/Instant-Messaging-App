
package client.controller;

import client.model.RegisterModel;
import client.view.RegisterView;
import server.model.UserModel;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// this will show the GUI for registering the user,
// and it will add the user to the data file

public class RegisterController {
    private final RegisterView registerView;
    private final RegisterModel registerModel;

    //Constructor for register controller
    public RegisterController(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        registerView = new RegisterView();
        registerModel = new RegisterModel(inputStream, outputStream);
        registerView.addRegisterListener(e -> {
            String username = registerView.getUsername();
            String password = registerView.getPassword();
            String reEnteredPass = registerView.getConfirmPassword();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(registerView.getContentPane(), "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(reEnteredPass)) {
                JOptionPane.showMessageDialog(registerView.getContentPane(), "Password did not match, try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Send request to server
                outputStream.writeObject("register");
                outputStream.writeObject(new UserModel(username, password));

                while (true) {
                    if (inputStream.readObject().equals("registered")) {
                        JOptionPane.showMessageDialog(null, "Registered user " + username, "Registered", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                }
                // Go back to login view
                registerView.dispose();
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        registerView.setVisible(true);
    }


}
