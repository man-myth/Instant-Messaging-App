package client.model;

import common.UserModel;

public class RegisterModel {

    public void register(String username, String pass1, String pass2){
        if(username.equals(""))
            System.out.println("Please enter a username");
        else if (!pass1.equals(pass2))
            System.out.println("Password did not match, try again.");
        else {
            System.out.println("Registration done.");
            UserModel userModel = new UserModel(username,pass1);
            //todo = add userModel to the server
        }
    }
}
