
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
            String username = registerView.getUsername(); //asd
            String password = registerView.getPassword(); //123
            String reEnteredPass = registerView.getConfirmPassword(); //123

            //checks if username field is empty
            boolean isUserEmpty = registerModel.isUserEmpty(username); //true

            //checks if passwords match
            boolean doesPassMatch = registerModel.doesPassMatch(password,reEnteredPass);

            //gui method that displays an error if there is an empty username or password mismatch
            boolean errorExist = registerView.promptError(isUserEmpty,doesPassMatch);

            //registers the user and checks if username already exist, cancels if there is existing an error
            boolean userValidity =registerModel.registerUser(username,password, errorExist);

            //gui method that displays an error if username already exist, cancels if there is existing an error
            registerView.isUserValid(userValidity, errorExist);

            //gui method that displays a successfully registered message, cancels if there is existing an error
            registerView.successRegister(username, errorExist, userValidity);
        });

        registerView.setVisible(true);
    }

}