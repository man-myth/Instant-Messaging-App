package client.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class LoginModel {
    public ObjectInputStream inputStream;
    public ObjectOutputStream outputStream;

    public LoginModel(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public boolean isUser(String username, String password) {
        try {
            String message = "";
            outputStream.writeObject(username);
            outputStream.writeObject(password);
            while (true) {
                message = (String) inputStream.readObject();
                if (message.equals("VERIFIED")) {
                    return true;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
