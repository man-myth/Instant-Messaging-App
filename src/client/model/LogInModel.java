package client.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class LogInModel {
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    Scanner kbd;
    public LogInModel(ObjectInputStream inputStream, ObjectOutputStream outputStream, Scanner kbd) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.kbd = kbd;
    }

    public boolean isUser(){
        try {
            String input = "";
            String message = "";
            while (true) {
                message = (String) inputStream.readObject();
                if(message.equals("VERIFIED")){
                    return true;
                }
                System.out.print(message);
                input = kbd.nextLine();
                outputStream.writeObject(input);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
