package client.model;

import common.UserModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LoginModel {
    public ObjectInputStream inputStream;
    public ObjectOutputStream outputStream;

    public LoginModel(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public boolean isUser(String username, String password) {
        try {
            outputStream.writeObject(username);
            outputStream.writeObject(password);
            String message = (String) inputStream.readObject();
            if (message.equals("VERIFIED")) {
                return true;
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserModel getUserModel() throws Exception {
        return (UserModel) inputStream.readObject();
    }
}
