package client.controller;

import client.model.ClientModel;
import client.view.AddContactToRoomView;
import client.view.ClientView;
import client.view.ExitOnCloseAdapter;
import client.view.KickContactFromRoomView;
import client.view.SettingsView;
import server.model.ChatRoomModel;
import server.model.MessageModel;
import server.model.ServerModel;
import server.model.UserModel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

//this class will now implement Runnable
public class ClientController implements Runnable {
    // -Fields
    private final Socket socket;
    ChatRoomModel currentRoom;
    ClientView clientView;
    ClientModel clientModel;
    AddContactToRoomView addToRoomView;
    KickContactFromRoomView kickUserView;
    SettingsView settingsView;
    SettingsView.AskNewName newName;
    SettingsView.AskNewPass newPass;

    // -Constructor
    public ClientController(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
                            UserModel user, ChatRoomModel publicChat) {
        this.socket = socket;
        this.currentRoom = publicChat;
        this.clientModel = new ClientModel(socket, inputStream, outputStream, user);
    }

    // -Methods

    // run method when calling client controller
    public void run() {
        System.out.println("Logged in with user: " + clientModel.getUser());
        clientView = new ClientView(clientModel.getUser(), currentRoom);
        clientView.setWindowAdapter(new ExitOnCloseAdapter(socket));

        //- settings actions
        clientView.settingsButtonListener(e -> {
            //asking new username listener
            SettingsView settingsView = new SettingsView();
            settingsView.changeNameActionListener(e1 -> {
                newName = new SettingsView.AskNewName(); // access the AskNewName class from SettingsView
                newName.changeListener(f -> { // action listener for the button in AskNewNAme
                    String enteredName = newName.getText();
                    String oldName = clientModel.getUser().getUsername();
                    boolean isChanged = clientModel.changeUsername(enteredName, oldName);
                    clientView.changeUsername(oldName,enteredName); //change button text of username
                    currentRoom.searchUser(oldName).setUsername(enteredName); //change the username from chatroom list
                    newName.changeSuccess(oldName, enteredName, isChanged);
                });
            });

            //asking new password listener
            settingsView.changePassActionListener(e2 -> {
                newPass = new SettingsView.AskNewPass(); // access the AskNewPass class from SettingsView
                newPass.changeListener(f -> { // action listener for the button in AskNewPass
                    String enteredPass = newPass.getPass();
                    String reEnteredPass = newPass.getRePass();
                    boolean isPassValid = clientModel.isPassValid(enteredPass, reEnteredPass); // checks if passwords match
                    newPass.promptError(isPassValid); // prompt an error if passwords do not match
                    clientModel.changePassword(enteredPass, isPassValid); // else, change password
                    currentRoom.searchUser(clientModel.getUser().getUsername()).setPassword(enteredPass);
                    newPass.changeSuccess(isPassValid);
                });
            });

        });


        //- adding of contact to a room actions
        clientView.setAddButtonActionListener(e -> {
            /*
             * once the add button to room is clicked,
             * get the contacts of the user and put it in the combo box view
             */
            String[] contactArray = clientModel.listToStringArrayAdd(clientModel.getUser().getContacts());
            addToRoomView = new AddContactToRoomView(contactArray);

            // calls the addContactToRoom method from client model if add button is clicked
            addToRoomView.setAddButtonActionListener(e1 -> {
                try {
                    String username = addToRoomView.getSelected();
                    UserModel newMember = clientModel.getUser().searchUserInContact(username);
                    boolean isUserHere = currentRoom.isUserHere(newMember);
                    if (isUserHere)
                        addToRoomView.errorUserIsHere();
                    else {
                        currentRoom.addUser(newMember);
                        clientView.addNewMember(newMember);
                        addToRoomView.successMessage();
                    }
                }catch (NullPointerException error){
                    addToRoomView.errorInvalidAction();
                }
            });
        });


        //- kick user from the room actions
        clientView.setKickButtonActionListener(e -> {
            String[] contactArray = clientModel.listToStringArrayAdd(currentRoom.getUsers());
            kickUserView = new KickContactFromRoomView(contactArray);

            kickUserView.setKickButtonActionListener(e1 -> {
                try {
                    String username = kickUserView.getSelected();
                    UserModel roomMember = currentRoom.searchUser(username);
                    currentRoom.kickUser(roomMember);
                    clientView.kickMember(roomMember);
                    kickUserView.successMessage();
                } catch (NullPointerException error) {
                    kickUserView.errorInvalidAction();
                }
            });
        });


        //- broadcasting messages actions
        clientView.setMessageListener(e -> {
            String message = clientView.getMessage();
            MessageModel msg = new MessageModel(clientModel.getUser(), currentRoom, message, LocalTime.now(),
                    LocalDate.now());
            boolean doBroadcast = clientModel.broadcastMessage(message, msg);
            if (doBroadcast) {
                clientView.addMessage(msg);
                clientView.clearTextArea();
            }
        });

        // Set ActionListener for member button popup menu
        clientView.setAddItemActionListener(e -> {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
            JButton invokerButton = (JButton) popupMenu.getInvoker();
            String username = invokerButton.getText();
            clientModel.addContact(username);
        });

        // Separate thread for GUI
        EventQueue.invokeLater(() -> clientView.setVisible(true));

        // Thread for receiving responses from the server
        new Thread(() -> {
            try {
                while (true) {
                    String event = clientModel.getEvent();
                    System.out.println(event);
                    if (event.equals("broadcast")) { // do this if event = "broadcast"
                        MessageModel message = clientModel.getMessageFromStream();
                        clientView.addMessage(message);
                    } else if (event.equals("contact added")) { // do this if event = "contact added"
                        clientModel.receiveContact();
                        System.out.println(clientModel.getUser().getContacts());
                        clientView.updateContacts(clientModel.getUser().getContacts());
                    }
                }
            } catch (Exception e) {
                System.out.println(socket + "has disconnected.");
                //e.printStackTrace();
            }
        }).start();
    }// end of run method

}// END OF CLIENT CONTROLLER
