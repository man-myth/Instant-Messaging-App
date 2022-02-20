package client.model;

import server.model.MessageModel;
import server.model.UserModel;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientModel{
    private Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    UserModel user;

    public ClientModel(Socket clientSocket, ObjectInputStream inputStream, ObjectOutputStream outputStream, UserModel user) {
        this.socket = clientSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.user = user;
    }

    /*
    changed; run() -> doEvent()
    returns a string to specify what event to do
    controller will read the event
    controller will tell model what method to run
     */
    public String doEvent(){
        Object msg;
        try {
            msg = inputStream.readObject();
            if (msg.equals("broadcast")) {
                return "broadcast";
            }else if (msg.equals("return contacts")) {
                return "return contacts";
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "none";
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    //added; method that gets message from stream
    public MessageModel getMessageFromStream() throws Exception {
        return (MessageModel) inputStream.readObject();
    }

    /*
    changed; get message from controller class
    returns true to tell controller
    to add message to client view and cleat text area
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


    public void addContactToRoom(String username){
        try {
            outputStream.writeObject("add contact to room");
            outputStream.writeObject(username);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /*
    takes the list of contacts and put their usernames in a String array
    for combo box view
    */
    public String[] contactsToStringArr(List<UserModel> list){
        ArrayList<String> contacts = new ArrayList<>();
        for(UserModel u: list){
            contacts.add(u.getUsername());
        }
        contacts.add("test");
        return contacts.toArray(new String[0]);
    }

}
