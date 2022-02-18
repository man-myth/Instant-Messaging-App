package client.model;

import server.model.UserModel;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AddContactToRoomModel {
    public void addUser(ObjectInputStream inputStream, ObjectOutputStream outputStream, String username) {
        try {
            outputStream.writeObject("add contact to room");
            //todo add user to chat room

            if(inputStream.readObject().equals("done"))
                System.out.println("lmao");


        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public String[] returnContacts(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        ArrayList<String> contacts = new ArrayList<>();
        try {
            outputStream.writeObject("get contacts");
            while (true){
                String input = (String)inputStream.readObject();
                if(input.equals("done")) break;
                else contacts.add(input);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return contacts.toArray(String[]::new);
    }
}

