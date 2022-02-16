package client.model;

import server.controller.ServerController;
import server.model.UserModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.util.Scanner;

public class RegisterModel {

    //method that registers the user
    public void registerUser(String username, String pass1, String pass2){
        //if username field is empty, prompt an error
        if(username.equals(""))
            System.out.println("Please enter a username");

        //if pass1 and pass2 did not match, prompt an error
        else if (!pass1.equals(pass2))
            System.out.println("Password did not match, try again.");

        //if info is valid, writes the user info to newUser.txt
        else {
            try {
                //creates the text file
                File newUserInfo = new File("res/newUser.txt");
                FileWriter myWriter = new FileWriter(newUserInfo);
                myWriter.write(username + "\n" + pass1);
                myWriter.close();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            System.out.println("Registration done.");
            //todo = add userModel to the server
        }
    }
}
