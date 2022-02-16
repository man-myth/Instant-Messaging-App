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
        try {
            while (true) {
                for (UserModel user : users) {
                    System.out.println(user);
                    if (user.getUsername().compareTo(username) == 0 && user.getPassword().compareTo(password) == 0) {
                        System.out.println("it's here");
                        outputStream.writeObject("VERIFIED");
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
