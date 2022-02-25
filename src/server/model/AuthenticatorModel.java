package server.model;

import common.UserModel;

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
    public String verifyUser(String username, String password) {
        if (username.length() == 0)
            return "enter username";
        for (UserModel user : users) {
            if (user.getUsername().compareTo(username) == 0 && user.getPassword().compareTo(password) == 0) {
                if (user.isActive()) {
                    return "is active";
                }
                user.setActive(true);
                return "verified";
            } else if (user.getUsername().equals(username) && !user.getPassword().equals(password))
                return "wrong pass";
        }
        return "does not exist";
    }

    public boolean toggleChangePass(int attempts) {
        return attempts % 3 == 0;
    }

    public boolean isPassValid(String pass, String rePass) {
        return pass.equals(rePass);
    }

    public void changePassword(String pass, boolean isValid, String username) {
        if (isValid) {
            UserModel u = new UserModel();
            for (UserModel user : users) {
                if (user.getUsername().equals(username))
                    u = user;
            }
            u.setPassword(pass);
            Utility.exportUsersData(users);
        }
    }


}
