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

    // takes the list of contacts and put their usernames in a String array
    // for combo box view
    public String[] listToStringArrayAdd(List<UserModel> list) {
        ArrayList<String> contacts = new ArrayList<>();
        for (UserModel u : list) {
            //continue if username is equals "your username" or "admin'
            if(u.getUsername().equals(user.getUsername()) || u.getUsername().equals("admin"))
                continue;
            contacts.add(u.getUsername());
        }
        return contacts.toArray(String[]::new);
    }


/*--- SETTINGS MODEL ---*/
    public boolean changeUsername(String newName, String oldName) {
        if (newName.length() != 0) {
            user.setUsername(newName);
            try{
                String[] names = {oldName,newName};
                outputStream.writeObject("update username");
                outputStream.writeObject(names);
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return false;
    }

    public boolean isPassValid(String pass, String rePass) {
        return pass.equals(rePass);
    }

    public void changePassword(String pass, boolean isValid) {
        if (isValid) {
            user.setPassword(pass);
            try{
                outputStream.writeObject("update password");
                outputStream.writeObject(user.getUsername());
                outputStream.writeObject(pass);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}// END OF CLIENT MODEL
