package client.model;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
//Adding contact to room
public class AddContactToRoomModel {
    public void addUser(ObjectInputStream inputStream, ObjectOutputStream outputStream, String userName) {
        try {
            outputStream.writeObject("add contact to room");
            //todo ; add user to chat room
            if (inputStream.readObject().equals("done")) {
                System.out.println("rawr");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] returnContacts(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        ArrayList<String> contacts = new ArrayList<String>();
        try {
            outputStream.writeObject("get contacts");
            while (true) {
                String input = (String) inputStream.readObject();
                if (input.equals("done"))
                    break;
                else
                    contacts.add(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts.toArray(String[]::new);
    }
}
