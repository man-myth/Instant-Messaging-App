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
     * This method checks if login credentials are valid
     * @return
     */
    public boolean verifyUser(String username, String password) throws IOException {
        for (UserModel user : users) {
            if (user.getUsername().compareTo(username) == 0 && user.getPassword().compareTo(password) == 0 & !user.isActive()){
                user.setActive(true);
                return true;
            }
        }
        return false;
    }

}
