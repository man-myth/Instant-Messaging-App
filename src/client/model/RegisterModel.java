package client.model;

import client.view.RegisterView;
import server.model.UserModel;
import server.model.Utility;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

public class RegisterModel {

    public ObjectInputStream inputStream;
    public ObjectOutputStream outputStream;

    public RegisterModel(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    //method that registers the user
    public void registerUser(RegisterView registerView) {
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
    }
}


