package server.model;

import javax.swing.*;
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
            if (user.getUsername().compareTo(username) == 0 && user.getPassword().compareTo(password) == 0){
                if (user.isActive()) {
                    JOptionPane.showMessageDialog(null, username + " is already logged in!","Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                user.setActive(true);
                return true;
            }
        }
        JOptionPane.showMessageDialog(null, username + " does not exist!","Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

}
