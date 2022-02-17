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
    public void registerUser(String username, String password, boolean isError) {
        if(isError) return; //if there is an error, do not proceed

        try {
            // Send request to server
            outputStream.writeObject("register");
            outputStream.writeObject(new UserModel(username, password));

            while (true) {
                if (inputStream.readObject().equals("registered")) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    //checks if username field is empty
    public boolean isUserEmpty(String username){
        return username.isEmpty();
    }

    //checks if password match
    public boolean doesPassMatch(String password, String reEnteredPass){
        return !password.equals(reEnteredPass);
    }

}


