package client.model;

import server.model.MessageModel;
import server.model.UserModel;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientModel {
    private Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    UserModel user;

    public ClientModel(Socket clientSocket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
                       UserModel user) {
        this.socket = clientSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.user = user;
    }

    /*
     * changed; run() -> getEvent()
     * returns a string to specify what event to do
     * controller will read the event
     * controller will tell model what method to run
     */
    public String getEvent() {
        String msg = "none";
        try {
            msg = (String) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    /*------------------------------- MODELS -------------------------------*/

    /*--- BROADCASTING OF MESSAGE MODEL ---*/
    // added; method that gets message from stream
    public MessageModel getMessageFromStream() throws Exception {
        return (MessageModel) inputStream.readObject();
    }

    /*
     * changed; get message from controller class
     * returns true to tell controller
     * to add message to client view and cleat text area
     */
    public boolean broadcastMessage(String message, MessageModel msg) {
        if (message.isEmpty()) {
            return false;
        }
        try {
            outputStream.writeObject("broadcast");
            outputStream.writeObject(msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /*--- ADDING CONTACT MODEL ---*/

    // adds the new user to contact list
    public void receiveContact() {
        UserModel newUser = null;
        try {
            newUser = (UserModel) inputStream.readObject();
            System.out.println(newUser.getUsername());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        user.getContacts().add(newUser);
    }

    public void addContact(String username) {
        try {
            outputStream.writeObject("add contact");
            outputStream.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*--- ADDING/KICKING OF CONTACT TO CHAT ROOM MODEL ---*/

    public UserModel getContact(String username) {
        for (UserModel u : user.getContacts()) {
            if (u.getUsername().equals(username))
                return u;
        }
        return new UserModel("null", "null");
    }

    public void kickContactFromRoom(String username) {
        try {
            outputStream.writeObject("kick contact from room");
            outputStream.writeObject(username);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // takes the list of contacts and put their usernames in a String array
    // for combo box view
    public String[] contactsToStringArr(List<UserModel> list) {
        List<String> contacts = new ArrayList<>();
        for (UserModel u : list) {
            //continue if username is equals your username/admin
            if (u.getUsername().equals(user.getUsername()) || u.getUsername().equals("admin"))
                continue;
            contacts.add(u.getUsername());
            System.out.println(u.getUsername());
        }
        contacts.add("test lang po boss");
        return contacts.toArray(String[]::new);
    }

    /*--- SETTINGS MODEL ---*/
    //todo: update changes in dat file @2213277
    public boolean changeUsername(String newName) {
        if (newName.length() != 0) {
            user.setUsername(newName);
            return true;
        }
        return false;
    }

    public boolean isPassValid(String pass, String rePass) {
        return pass.equals(rePass);
    }

    public void changePassword(String pass, boolean isValid) {
        if (isValid) {
            System.out.println(pass);
            // todo change password @2213277
        }
    }

}// END OF CLIENT MODEL
