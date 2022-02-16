package server.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;


public class AuthenticatorModel {
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    List<UserModel> users;

    public AuthenticatorModel(ObjectInputStream inputStream, ObjectOutputStream outputStream, List<UserModel> users) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.users = users;
    }

    public boolean verifyUser(){
        try {
            while (true) {
                outputStream.writeObject("username: ");
                String username = (String) inputStream.readObject();
                outputStream.writeObject("password: ");
                String password = (String) inputStream.readObject();

                for (UserModel user : users) {
                    if (user.getUsername().compareTo(username) == 0 && user.getPassword().compareTo(password) == 0) {
                        outputStream.writeObject("VERIFIED");
                        return true;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

}
