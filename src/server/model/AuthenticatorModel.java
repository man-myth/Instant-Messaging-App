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

    /***
     * Checks if user is in the database
     * @return
     */
    public boolean verifyUser(String username, String password) {
        for (UserModel user : users) {
            if (user.getUsername().compareTo(username) == 0 && user.getPassword().compareTo(password) == 0) {
                return true;
            }
        }
        return false;
    }

}
