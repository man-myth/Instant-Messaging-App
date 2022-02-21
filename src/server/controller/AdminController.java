package server.controller;

import client.view.*;
import client.model.ClientModel;
import server.model.*;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import server.model.UserModel;
import server.view.AdminView;
import client.model.ClientModel;

import javax.swing.*;


public class AdminController {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private UserModel admin;
    ChatRoomModel currentRoom;
    AdminView adminView;
    AdminModel adminModel;
    AddContactToRoomView addToRoomView;
    KickContactFromRoomView kickUserView;
    SettingsView.AskNewName newName;
    SettingsView.AskNewPass newPass;


    public AdminController(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
                           UserModel user, ChatRoomModel publicChat) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.currentRoom = publicChat;
        this.adminModel = new AdminModel(socket, inputStream, outputStream, user);
    }



    public void run() {
        System.out.println("Logged in with user: " + adminModel.getUser());
        adminView = new AdminView(adminModel.getUser(), currentRoom);
        adminView.setWindowAdapter(new ExitOnCloseAdapter(socket));

        //- settings actions
        adminView.settingsButtonListener(e -> {
            //asking new username listener
            SettingsView settingsView = new SettingsView();
            settingsView.changeNameActionListener(e1 -> {
                newName = new SettingsView.AskNewName(); // access the AskNewName class from SettingsView
                newName.changeListener(f -> { // action listener for the button in AskNewNAme
                    String enteredName = newName.getText();
                    String oldName = adminModel.getUser().getUsername();
                    boolean isChanged = adminModel.changeUsername(enteredName, oldName);
                    if(isChanged) {
                        adminView.changeUsername(oldName, enteredName); //change button text of username
                        currentRoom.searchUser(oldName).setUsername(enteredName); //change the username from chatroom list
                        newName.changeSuccess(oldName, enteredName);
                    }else{
                        newName.promptError();
                    }
                });
            });

            //asking new password listener
            settingsView.changePassActionListener(e2 -> {
                newPass = new SettingsView.AskNewPass(); // access the AskNewPass class from SettingsView
                newPass.changeListener(f -> { // action listener for the button in AskNewPass
                    String enteredPass = newPass.getPass();
                    String reEnteredPass = newPass.getRePass();
                    boolean isPassValid = adminModel.isPassValid(enteredPass, reEnteredPass); // checks if passwords match
                    newPass.promptError(isPassValid); // prompt an error if passwords do not match
                    adminModel.changePassword(enteredPass, isPassValid); // else, change password
                    currentRoom.searchUser(adminModel.getUser().getUsername()).setPassword(enteredPass);
                    newPass.changeSuccess(isPassValid);
                });
            });

        });

        //- adding of contact to a room actions
        adminView.setAddButtonActionListener(e -> {
            /*
             * once the add button to room is clicked,
             * get the contacts of the user and put it in the combo box view
             */
            String[] contactArray = adminModel.listToStringArrayAdd(adminModel.getUser().getContacts());
            addToRoomView = new AddContactToRoomView(contactArray);

            // calls the addContactToRoom method from client model if add button is clicked
            addToRoomView.setAddButtonActionListener(e1 -> {
                try {
                    String username = addToRoomView.getSelected();
                    UserModel newMember = adminModel.getUser().searchUserInContact(username);
                    boolean isUserHere = currentRoom.isUserHere(username);
                    if (isUserHere)
                        addToRoomView.errorUserIsHere();
                    else {
                        currentRoom.addUser(newMember);
                        adminView.addNewMember(newMember);
                        addToRoomView.successMessage();
                    }
                }catch (NullPointerException error){
                    addToRoomView.errorInvalidAction();
                }
            });
        });

        //- kick user from the room actions
        adminView.setKickButtonActionListener(e2 -> {
            String[] contactArray = adminModel.listToStringArrayAdd(currentRoom.getUsers());
            kickUserView = new KickContactFromRoomView(contactArray);

            kickUserView.setKickButtonActionListener(e1 -> {
                try {
                    String username = kickUserView.getSelected();
                    UserModel roomMember = currentRoom.searchUser(username);
                    currentRoom.kickUser(roomMember);
                    adminView.kickMember(roomMember);
                    kickUserView.successMessage();
                } catch (NullPointerException error) {
                    kickUserView.errorInvalidAction();
                }
            });
        });

        //- broadcasting messages actions
        adminView.setMessageListener(e -> {
            String message = adminView.getMessage();
            MessageModel msg = new MessageModel(adminModel.getUser(), currentRoom, message, LocalTime.now(),
                    LocalDate.now());
            boolean doBroadcast = adminModel.broadcastMessage(message, msg);
            if (doBroadcast) {
                adminView.addMessage(msg);
                adminView.clearTextArea();
            }
        });

        // Set ActionListener for member button popup menu
        adminView.setAddItemActionListener(e -> {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
            JButton invokerButton = (JButton) popupMenu.getInvoker();
            String username = invokerButton.getText();
            adminModel.addContact(username);
        });
        // Separate thread for GUI
        EventQueue.invokeLater(() -> adminView.setVisible(true));

        // Thread for receiving responses from the server
        new Thread(() -> {
            try {
                while (true) {
                    String event = adminModel.getEvent();
                    System.out.println(event);
                    if (event.equals("broadcast")) { // do this if event = "broadcast"
                        MessageModel message = adminModel.getMessageFromStream();
                        adminView.addMessage(message);
                    } else if (event.equals("contact added")) { // do this if event = "contact added"
                        adminModel.receiveContact();
                        System.out.println(adminModel.getUser().getContacts());
                        adminView.updateContacts(adminModel.getUser().getContacts());
                    }
                }
            } catch (Exception e) {
                System.out.println(socket + "has disconnected.");
                //e.printStackTrace();
            }
        }).start();

    }

    //remove?
    public void receiveMessage(MessageModel message) {
        adminView.addMessage(message);
    }

    public void broadcastMessage() {
        String message = adminView.getMessage();
        if (message.isEmpty()) {
            return;
        }
        try {
            outputStream.writeObject("broadcast");
            MessageModel msg = new MessageModel(adminModel.getUser(), currentRoom, message, LocalTime.now(), LocalDate.now());
            outputStream.writeObject(msg);
            adminView.addMessage(msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        adminView.clearTextArea();
    }
}

