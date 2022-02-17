
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
    public RegisterController(ObjectInputStream inputStream, ObjectOutputStream outputStream){
        registerView = new RegisterView();
        registerModel = new RegisterModel(inputStream, outputStream);
        registerView.addRegisterListener(e -> {
            String username = registerView.getUsername();
            String password = registerView.getPassword();
            String reEnteredPass = registerView.getConfirmPassword();

            //checks if username field is empty
            boolean isUserEmpty = registerModel.isUserEmpty(username);

            //checks if passwords match
            boolean doesPassMatch = registerModel.doesPassMatch(password,reEnteredPass);

            //prompt an error if there is an error
            boolean isError = registerView.promptError(isUserEmpty,doesPassMatch);

            //registers the user, cancels if there is existing an error
            boolean userValidity =registerModel.registerUser(username,password, isError);

            //checks if username already exist, cancels if there is existing an error
            registerView.isUserValid(userValidity, isError);

            //displays a successfully registered message, cancels if there is existing an error
            registerView.successRegister(username, isError, userValidity);
        });

        registerView.setVisible(true);
    }

}