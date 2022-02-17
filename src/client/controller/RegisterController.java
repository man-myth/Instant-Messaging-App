
package client.controller;

import client.model.RegisterModel;
import client.view.RegisterView;
import server.model.UserModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
            boolean isUserEmpty = registerModel.isUserEmpty(username); //checks if username field is empty
            boolean doesPassMatch = registerModel.doesPassMatch(password,reEnteredPass); //checks if passwords match
            boolean isError = registerView.promptError(isUserEmpty,doesPassMatch); //prompt an error if there is an error
            registerModel.registerUser(username,password, isError); //registers the user, cancels if there is an error
            registerView.successRegister(username, isError); //displays a successfully registered message, cancels if there is an error
        });

        registerView.setVisible(true);
    }

}