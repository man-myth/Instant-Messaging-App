
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

            //if username field is empty, set to true
            boolean isUserEmpty = registerModel.isUserEmpty(username);

            //if passwords match, set to true
            boolean doesPassMatch = registerModel.doesPassMatch(password,reEnteredPass);

            //if there is an empty username or password mismatch, displays an error and set to true
            boolean errorExist = registerView.promptError(isUserEmpty,doesPassMatch);

            //if username already exist, do not registers the user and set to false; cancels if there is existing an error
            boolean userValidity =registerModel.registerUser(username,password, errorExist);

            //if username already exists, display an error; cancels if there is existing an error
            registerView.isUserValid(userValidity, errorExist);

            //that display a message if successfully registered; cancels if there is existing an error
            registerView.successRegister(username, errorExist, userValidity);
        });

        registerView.setVisible(true);
    }

}