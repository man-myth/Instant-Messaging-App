package server.controller;

import client.view.SettingsView;
import server.model.AuthenticatorModel;
import server.model.UserModel;
import server.view.AuthenticatorView;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class AuthenticatorController {
    AuthenticatorModel model;
    AuthenticatorView view;
    AuthenticatorView.AskNewPass newPass;

    public AuthenticatorController(ObjectInputStream inputStream, ObjectOutputStream outputStream, List<UserModel> users){
        model = new AuthenticatorModel(inputStream,outputStream,users);
        view = new AuthenticatorView();
    }

    public boolean isVerified(String username, String password) throws IOException {
        String isVerified = model.verifyUser(username,password);
        if(isVerified.equals("enter username")){
            view.promptEnterUsername();
            return false;
        }

        if(isVerified.equals("is active")) {
            view.promptAlreadyLoggedIn(username, password);
            return false;
        }

        if(isVerified.equals("wrong pass")){
            view.promptWrongPassword();
            return false;
        }

        if(isVerified.equals("does not exist")){
            view.promptDoesNotExist(username);
            return false;
        }
        return true;
    }

    public void toggleChangePass(int attempts, String username){
        if(model.toggleChangePass(attempts)){
            int ans =view.promptChangePass();
            if(ans == JOptionPane.YES_OPTION){
                newPass = new AuthenticatorView.AskNewPass(); // access the AskNewPass class from SettingsView
                newPass.changeListener(f -> { // action listener for the button in AskNewPass
                    String enteredPass = newPass.getPass();
                    String reEnteredPass = newPass.getRePass();
                    boolean isPassValid = model.isPassValid(enteredPass, reEnteredPass); // checks if passwords match
                    newPass.promptError(isPassValid); // prompt an error if passwords do not match
                    model.changePassword(enteredPass, isPassValid, username); // else, change password
                    newPass.changeSuccess(isPassValid);
                });
            }
        }
    }

}
