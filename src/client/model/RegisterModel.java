package client.model;

import server.model.UserModel;
import server.model.Utility;

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

    //method that adds user to the server
    public void addUser() {

    }

    //method that registers the user
    public void registerUser(String username, String pass1, String pass2) {
        //if username field is empty, prompt an error
        if (username.equals(""))
            System.out.println("Please enter a username");

        //if pass1 and pass2 did not match, prompt an error
        else if (!pass1.equals(pass2))
            System.out.println("Password did not match, try again.");

        //if info is valid, add the user to the server
        else {
            System.out.println("Registration done.");
            UserModel userModel = new UserModel(username, pass1);
        }
    }
}


