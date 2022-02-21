package client.controller;

import client.model.ClientModel;
import client.view.AddContactToRoomView;
import client.view.ClientView;
import client.view.ExitOnCloseAdapter;
import client.view.KickContactFromRoomView;
import client.view.SettingsView;
import server.model.ChatRoomModel;
import server.model.MessageModel;
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

        // -action for settings button
        clientView.settingsButtonListener(e -> {
            SettingsView settingsView = new SettingsView();
            // Action Listener for asking new username
            settingsView.changeNameActionListener(e1 -> {
                newName = new SettingsView.AskNewName(); // access the AskNewName class from SettingsView
                newName.changeListener(f -> { // action listener for the button in AskNewNAme
                    String enteredName = newName.getText();
                    String oldName = clientModel.getUser().getUsername();
                    boolean isChanged = clientModel.changeUsername(enteredName);
                    newName.changeSuccess(oldName, enteredName, isChanged);
                });
            });

            // Action Listener for asking new password
            settingsView.changePassActionListener(e2 -> {
                newPass = new SettingsView.AskNewPass(); // access the AskNewPass class from SettingsView
                newPass.changeListener(f -> { // action listener for the button in AskNewPass
                    String enteredPass = newPass.getPass();
                    String reEnteredPass = newPass.getRePass();
                    boolean isPassValid = clientModel.isPassValid(enteredPass, reEnteredPass); // checks if passwords
                    // match
                    newPass.promptError(isPassValid); // prompt an error if passwords do not match
                    clientModel.changePassword(enteredPass, isPassValid); // else, change password
                });
            });

        });

        // -action for adding of contact to a room
        clientView.setAddButtonActionListener(e -> {
            /*
             * once the add button to room is clicked,
             * get the contacts of the user and put it in the combo box view
             */
            String[] contactArray = clientModel.contactsToStringArr(clientModel.getUser().getContacts());
            addToRoomView = new AddContactToRoomView(contactArray);

            // calls the addContactToRoom method from client model if add button is clicked
            //todo kick gui will error after clicking add button, fix getContact returns null @2213277
            addToRoomView.setAddButtonActionListener(e1 -> {
                String username = addToRoomView.getSelected();
                UserModel newUser = clientModel.getContact(username);
                clientModel.getCurrentRoom().addUser(newUser);
                addToRoomView.successMessage();
            });
        });

        // kick user from the room
        clientView.setKickButtonActionListener(e -> {
            clientModel.getCurrentRoom().getUsers().add(new UserModel("mat", "123"));
            clientModel.getCurrentRoom().getUsers().add(new UserModel("lmao", "123"));
            String[] contactArray = clientModel.contactsToStringArr(clientModel.getCurrentRoom().getUsers());
            kickUserView = new KickContactFromRoomView(contactArray);

            kickUserView.setKickButtonActionListener(e1 -> {
                String username = kickUserView.getSelected();
                clientModel.getCurrentRoom().kickUser(username);
                kickUserView.successMessage();
            });
        });

        // -action for broadcasting messages
        clientView.setMessageListener(new MessageListener());

        // Set ActionListener for member button popup menu
        clientView.setAddItemActionListener(e -> {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
            JButton invokerButton = (JButton) popupMenu.getInvoker();
            String username = invokerButton.getText();
            clientModel.addContact(username);
        });

        // Set ActionListener for contact buttons
        clientView.setContactButtonsActionListener(new ContactButtonActionListener());

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
                        clientModel.updateChatRooms();
                        clientView.updateContacts(clientModel.getUser().getChatRooms());

                        // Re-set action listeners
                        clientView.setContactButtonsActionListener(new ContactButtonActionListener());
                    } else if (event.equals("new message")) {

                    } else if (event.equals("return room")) {
                        clientModel.receiveRoom();
                        clientView.updateRoom(clientModel.getCurrentRoom());

                        // Re-set action listeners
                        clientView.setMessageListener(new MessageListener());
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

}// END OF CLIENT CONTROLLER
