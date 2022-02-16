package client.model;

import server.model.UserModel;

public class RegisterModel {

    //method that registers the user
    public void registerUser(String username, String pass1, String pass2){
        //if username field is empty, prompt an error
        if(username.equals(""))
            System.out.println("Please enter a username");

        //if pass1 and pass2 did not match, prompt an error
        else if (!pass1.equals(pass2))
            System.out.println("Password did not match, try again.");

        //if info is valid, add the user to the server
        else {
            System.out.println("Registration done.");
            UserModel userModel = new UserModel(username,pass1);
            //todo = add userModel to the server
        }
    }
}
