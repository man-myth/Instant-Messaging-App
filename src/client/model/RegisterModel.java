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

    //method that checks if username already exists, if not, registers the user; returns true if successfully registered
    public boolean registerUser(String username, String password, boolean errorExist) {
        if(errorExist) return false; //if there is an existing error, do not proceed
        try {
            // Send request to server
            outputStream.writeObject("register");
            outputStream.writeObject(new UserModel(username, password));
            String status = (String)inputStream.readObject();

            while (true) {
                if (status.equals("registered")) {
                    return true;
                } else if(status.equals("invalid")){
                    return false;
                }

            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    //returns true if username field is empty
    public boolean isUserEmpty(String username){
        return username.isEmpty();
    }

    //returns true if password match
    public boolean doesPassMatch(String password, String reEnteredPass){
        return !password.equals(reEnteredPass);
    }



}


