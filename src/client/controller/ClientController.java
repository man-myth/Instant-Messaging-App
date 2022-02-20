package client.controller;

import client.model.ClientModel;
import client.view.AddContactToRoomView;
import client.view.ClientView;
import client.view.ExitOnCloseAdapter;
import client.view.SettingsView;
import server.model.ChatRoomModel;
import server.model.MessageModel;
import server.model.UserModel;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

//this class will now implement Runnable
public class ClientController implements Runnable{
    //-Fields
    private final Socket socket;
    ChatRoomModel currentRoom;
    ClientView clientView;
    ClientModel clientModel;
    AddContactToRoomView addToRoomView;
    SettingsView settingsView;
    SettingsView.AskNewName newName;
    SettingsView.AskNewPass newPass;

    //-Constructor
    public ClientController(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
            UserModel user, ChatRoomModel publicChat) {
        this.socket = socket;
        this.currentRoom = publicChat;
        this.clientModel = new ClientModel(socket, inputStream, outputStream, user);
    }

    //-Methods

    //run method when calling client controller
    @Override
    public void run() {
        System.out.println("Logged in with user: " + clientModel.getUser());
        clientView = new ClientView(clientModel.getUser(), currentRoom);
        clientView.setWindowAdapter(new ExitOnCloseAdapter(socket));

        //-action for settings button
        clientView.settingsButtonListener(e ->{
            SettingsView settingsView = new SettingsView();
            //Action Listener for asking new username
            settingsView.changeNameActionListener(e1 -> {
                newName = new SettingsView.AskNewName(); //access the AskNewName class from SettingsView
                newName.changeListener(f -> { //action listener for the button in AskNewNAme
                    String enteredName = newName.getText();
                    clientModel.changeUsername(enteredName);
                    System.out.println(enteredName);
                });
            });

            //Action Listener for asking new password
            settingsView.changePassActionListener(e2 -> {
                newPass = new SettingsView.AskNewPass(); //access the AskNewPass class from SettingsView
                newPass.changeListener(f -> { //action listener for the button in AskNewPass
                    String enteredPass = newPass.getPass();
                    String reEnteredPass = newPass.getRePass();
                    boolean isPassValid = clientModel.isPassValid(enteredPass,reEnteredPass); //checks if passwords match
                    newPass.promptError(isPassValid); //prompt an error if passwords do not match
                    clientModel.changePassword(enteredPass,isPassValid); //else, change password
                });
            });

        });


        //-action for adding of contact to a room
        clientView.setAddButtonActionListener(e -> {
            /*
            once the add button to room is clicked,
            get the contacts of the user and put it in the combo box view
            */
            String[] contactArray = clientModel.contactsToStringArr(clientModel.getUser().getContacts());
            addToRoomView = new AddContactToRoomView(contactArray);

            //calls the addContactToRoom method from client model if add button is clicked
            addToRoomView.setAddButtonActionListener(e1 -> {
                String username = addToRoomView.getSelected();
                clientModel.addContactToRoom(username);
            });
        });

        //-action for broadcasting messages
        clientView.setMessageListener(e -> {
            String message = clientView.getMessage();
            MessageModel msg = new MessageModel(clientModel.getUser(), currentRoom, message, LocalTime.now(), LocalDate.now());
            boolean doBroadcast = clientModel.broadcastMessage(message, msg);
            if(doBroadcast){
                clientView.addMessage(msg);
                clientView.clearTextArea();
            }
        });
        EventQueue.invokeLater(() -> clientView.setVisible(true));

        //-loop for broadcasting messages
        new Thread(() ->{
            while (true){
                try {
                    String event = clientModel.doEvent();
                    receiveMessage(event); //call if event = "broadcast"
                    addContacts(event); //call if event = "contact added"

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }//end of run method

    //calls the getMessageFromStream() method from model and adds the message to view
    public void receiveMessage(String event) throws Exception {
        if(event.equals("broadcast")){
            MessageModel message = clientModel.getMessageFromStream();
            clientView.addMessage(message);
        }
    }

    //calls the getContact() method from model and updates the view
    public void addContacts(String event) throws Exception{
        if(event.equals("contact added")){
            clientModel.addContact();
            clientView.updateContacts(clientModel.getUser().getContacts());
        }
    }
}//END OF CLIENT CONTROLLER
