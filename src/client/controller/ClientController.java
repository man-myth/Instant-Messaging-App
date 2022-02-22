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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

//this class will now implement Runnable
public class ClientController implements Runnable {
    // -Fields
    private final Socket socket;

    ClientView clientView;
    ClientModel clientModel;
    AddContactToRoomView addToRoomView;
    KickContactFromRoomView kickUserView;
    SettingsView settingsView;
    SettingsView.AskNewName newName;
    SettingsView.AskNewPass newPass;
    SettingsView.HelpModule helpModule;

    // -Constructor
    public ClientController(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
                            UserModel user, ChatRoomModel publicChat) {
        this.socket = socket;
        this.clientModel = new ClientModel(socket, inputStream, outputStream, user, publicChat);
    }

    // -Methods

    // run method when calling client controller
    public void run() {
        System.out.println("Logged in with user: " + clientModel.getUser());
        clientView = new ClientView(clientModel.getUser(), clientModel.getCurrentRoom());
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
                    if (isChanged) {
                        clientView.changeUsername(oldName, enteredName); //change button text of username
                        clientModel.getCurrentRoom().searchUser(oldName).setUsername(enteredName); //change the username from chatroom list
                        newName.changeSuccess(oldName, enteredName);
                    } else {
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
                    boolean isPassValid = clientModel.isPassValid(enteredPass, reEnteredPass); // checks if passwords match
                    newPass.promptError(isPassValid); // prompt an error if passwords do not match
                    clientModel.changePassword(enteredPass, isPassValid); // else, change password
                    clientModel.getCurrentRoom().searchUser(clientModel.getUser().getUsername()).setPassword(enteredPass);
                    newPass.changeSuccess(isPassValid);
                });
            });


            settingsView.helpActionListener(e3 -> {
                helpModule = new SettingsView.HelpModule(); // access the HelpModule class from SettingsView

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
                    boolean isUserHere = clientModel.getCurrentRoom().isUserHere(username);
                    if (isUserHere)
                        addToRoomView.errorUserIsHere();
                    else {
                        clientModel.getCurrentRoom().addUser(newMember);
                        clientView.addNewMember(newMember);
                        addToRoomView.successMessage();
                    }
                } catch (NullPointerException error) {
                    addToRoomView.errorInvalidAction();
                }
            });
        });


        //- kick user from the room actions
        clientView.setKickButtonActionListener(e -> {
            String[] contactArray = clientModel.listToStringArrayAdd(clientModel.getCurrentRoom().getUsers());
            kickUserView = new KickContactFromRoomView(contactArray);

            kickUserView.setKickButtonActionListener(e1 -> {
                try {
                    String username = kickUserView.getSelected();
                    UserModel roomMember = clientModel.getCurrentRoom().searchUser(username);
                    clientModel.getCurrentRoom().kickUser(roomMember);
                    clientView.kickMember(roomMember);
                    kickUserView.successMessage();
                } catch (NullPointerException error) {
                    kickUserView.errorInvalidAction();
                }
            });
        });


        //- broadcasting messages actions
        clientView.setMessageListener(new MessageListener());

        // Set ActionListener for member button popup menu
        clientView.setAddItemActionListener(new AddContactListener());

        // Set ActionListener for contact buttons
        clientView.setContactButtonsActionListener(new ContactButtonActionListener());

        // Set ActionListener for contact button popup menu
        clientView.setContactPopUpButtonsActionListener(new AddBookmarkListener());


        // Separate thread for GUI
        EventQueue.invokeLater(() -> clientView.setVisible(true));

        // Thread for receiving responses from the server
        new Thread(() -> {
            try {
                while (true) {
                    String event = clientModel.getEvent();
                    System.out.println("Event: " + event);
                    if (event.equals("broadcast")) { // do this if event = "broadcast"
                        MessageModel message = clientModel.getMessageFromStream();
                        if (clientModel.getCurrentRoom().getName().equalsIgnoreCase("Public Chat")) {
                            clientView.addMessage(message);
                        }

                    } else if (event.equals("contact added")) { // do this if event = "contact added"
                        clientModel.updateUser();
                        clientView.updateContacts(clientModel.getUser());

                        // Re-set action listeners
                        clientView.setContactButtonsActionListener(new ContactButtonActionListener());
                        clientView.setContactPopUpButtonsActionListener(new AddBookmarkListener());
                    }else if (event.equals("bookmark added")) { // do this if event = "bookmark added"
                        clientModel.updateUser();
                        clientView.updateContacts(clientModel.getUser());
                        // Re-set action listeners
                        clientView.setContactButtonsActionListener(new ContactButtonActionListener());
                        clientView.setContactPopUpButtonsActionListener(new AddBookmarkListener());
                    } else if (event.equals("return room")) {
                        clientModel.receiveRoom();
                        clientView.updateRoom(clientModel.getCurrentRoom());

                        // Re-set action listeners
                        clientView.setAddItemActionListener(new AddContactListener());
                        clientView.setMessageListener(new MessageListener());
                    } else if (event.equals("new message")) {
                        // Update GUI
                        MessageModel message = clientModel.getMessageFromStream();
                        if (message.getReceiver().getName().equalsIgnoreCase(clientModel.getCurrentRoom().getName())) {
                            clientView.addMessage(message);
                        }
                    }
                }
            } catch (Exception e) {
                //System.out.println(socket + "has disconnected.");
                e.printStackTrace();
            }
        }).start();
    }// end of run method

    class ContactButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String room = ((JButton) e.getSource()).getText();
            if (room.equals(clientModel.getCurrentRoom().getName())) {
                return;
            }
            clientModel.requestRoom(room);
        }
    }

    class MessageListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String message = clientView.getMessage();
            ChatRoomModel currentRoom = clientModel.getCurrentRoom();
            MessageModel msg = new MessageModel(clientModel.getUser(), currentRoom, message, LocalTime.now(),
                    LocalDate.now());

            // Public chat
            if (currentRoom.getName().equalsIgnoreCase("Public Chat")) {
                boolean doBroadcast = clientModel.broadcastMessage(msg);
                if (doBroadcast) {
                    clientView.addMessage(msg);
                    clientView.clearTextArea();
                }
                // Conference or Private Message
            } else {
                clientModel.sendMessage(msg);
                clientView.addMessage(msg);
                clientView.clearTextArea();
            }
        }
    }

    class AddContactListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
            JButton invokerButton = (JButton) popupMenu.getInvoker();
            String username = invokerButton.getText();
            clientModel.addContact(username);
        }
    }

    class AddBookmarkListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Inside bookmark listener");
            JMenuItem menuItem = (JMenuItem) e.getSource();
            JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
            JButton invokerButton = (JButton) popupMenu.getInvoker();
            String username = invokerButton.getText();
            System.out.println("Bookmark " + username);
            clientModel.addBookmark(username);
        }
    }
}// END OF CLIENT CONTROLLER
