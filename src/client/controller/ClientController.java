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
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

//this class will now implement Runnable
public class ClientController implements Runnable {
    // -Fields
    private final Socket socket;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;

    ChatRoomModel currentRoom;
    ClientView clientView;
    ClientModel clientModel;
    AddContactToRoomView addToRoomView;
    KickContactFromRoomView kickUserView;
    SettingsView settingsView;
    SettingsView.AskNewName newName;
    SettingsView.AskNewPass newPass;
    SettingsView.HelpModule helpModule;
    SettingsView.StatusView statusView;

    // -Constructor
    public ClientController(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
                            UserModel user, ChatRoomModel publicChat) {
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.currentRoom = publicChat;
        this.clientModel = new ClientModel(socket, inputStream, outputStream, user, publicChat);

    }

    // -Methods

    // run method when calling client controller
    public void run() {
        System.out.println("Logged in with user: " + clientModel.getUser());
        clientView = new ClientView(clientModel.getUser(), clientModel.getCurrentRoom());
        clientView.setWindowAdapter(new ExitOnCloseAdapter(socket));
        clientView.setStatusImage(clientModel.getUser().getUsername(), clientModel.getUser().getStatus());
        clientModel.changeStatus("Online");
        clientModel.readAllStatus();

        //- settings actions
        clientView.settingsButtonListener(e -> {
            //asking new username listener
            settingsView = new SettingsView();
            settingsView.changeNameActionListener(e1 -> {
                newName = new SettingsView.AskNewName(); // access the AskNewName class from SettingsView
                newName.changeListener(f -> { // action listener for the button in AskNewNAme
                    String enteredName = newName.getText();
                    String oldName = clientModel.getUser().getUsername();
                    boolean isChanged = clientModel.changeUsername(enteredName, oldName);
                    if (isChanged) {
                        clientView.changeUsername(oldName, enteredName); //change button text of username
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
                    newPass.changeSuccess(isPassValid);
                });
            });


            //set status listener
            settingsView.changeStatusActionListener(new SetStatusListener());

            //help module display
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
                        clientModel.addContactToRoom(newMember, clientModel.getCurrentRoom().getName());
                        if (!clientModel.getCurrentRoom().getAdmin().equals("")) {
                            addToRoomView.successMessage();
                        }
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
                    if (clientModel.isAdmin(clientModel.getUser())) {
                        String username = kickUserView.getSelected();
                        UserModel roomMember = clientModel.getCurrentRoom().searchUser(username);
                        clientModel.getCurrentRoom().kickUser(roomMember);
                        clientView.kickMember(roomMember);
                        kickUserView.successMessage();
                    } else {
                        clientView.noPermsMsg();
                    }

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

        //members search bar text listener
        clientView.membersSearchActionListener(new MembersSearchTextListener());

        //members search bar text listener
        clientView.contactsSearchListener(new ContactsSearchListener());

        // Set ActionListener for contact button popup menu
        clientView.setBookmarkButtonActionListener(new AddBookmarkListener());

        clientView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());

        clientView.setRemoveContactButtonActionListener(new RemoveContactListener());

        clientView.setLogOutListener(new LogOutListener());

        // Separate thread for GUI
        EventQueue.invokeLater(() -> clientView.setVisible(true));

        // Thread for receiving responses from the server
        new Thread(() -> {
            try {
                while (clientModel.getUser().isActive()) {
                    String event = clientModel.getEvent();
                    System.out.println("Event: " + event);
                    if (event.equals("broadcast")) { // do this if event = "broadcast"
                        MessageModel message = clientModel.getMessageFromStream();
                        if (clientModel.getCurrentRoom().getName().equalsIgnoreCase("Public Chat")) {
                            clientView.addMessage(message);
                        }

                    } else if (event.equals("contact added")) { // do this if event = "contact added"
                        clientModel.updateChatRooms();
                        clientModel.updateContacts();
                        clientView.updateContacts(clientModel.getUser());

                        clientView.setContactButtonsActionListener(new ContactButtonActionListener());
                        clientView.setBookmarkButtonActionListener(new AddBookmarkListener());
                        clientView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());
                        clientView.setRemoveContactButtonActionListener(new RemoveContactListener());
                        clientView.contactsSearchListener(new ContactsSearchListener());

                    } else if (event.equals("adding self")) {
                        clientView.showErrorMessage("You are adding yourself!");
                    } else if (event.equals("contact updated")) { // do this if event = "contact added/removed"
                        clientModel.updateUser();
                        clientView.updateContacts(clientModel.getUser());

                        // Re-set action listeners
                        clientView.setContactButtonsActionListener(new ContactButtonActionListener());
                        clientView.setBookmarkButtonActionListener(new AddBookmarkListener());
                        clientView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());
                        clientView.setRemoveContactButtonActionListener(new RemoveContactListener());
                        clientView.contactsSearchListener(new ContactsSearchListener());
                    } else if (event.equals("bookmark updated")) { // do this if event = "bookmark added/removed"
                        clientModel.updateUser();
                        System.out.println("updating contacts....");
                        clientView.updateContacts(clientModel.getUser());
                        // Re-set action listeners
                        clientView.setContactButtonsActionListener(new ContactButtonActionListener());
                        clientView.setBookmarkButtonActionListener(new AddBookmarkListener());
                        clientView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());
                        clientView.setRemoveContactButtonActionListener(new RemoveContactListener());
                        clientView.contactsSearchListener(new ContactsSearchListener());
                    } else if (event.equals("return room")) {
                        clientModel.receiveRoom();
                        clientView.updateRoom(clientModel.getCurrentRoom());

                        // Re-set action listeners
                        clientView.setAddItemActionListener(new AddContactListener());
                        clientView.setMessageListener(new MessageListener());
                    } else if (event.equals("new message")) {
                        // Update GUI if current room has new message
                        MessageModel message = clientModel.getMessageFromStream();
                        if (message.getReceiver().getName().equalsIgnoreCase(clientModel.getCurrentRoom().getName())) {
                            clientView.addMessage(message);
                        }
                    } else if (event.equals("update chat rooms")) {
                        clientModel.updateChatRooms();
                        clientView.updateContacts(clientModel.getUser());

                        // Re-set action listeners
                        clientView.setContactButtonsActionListener(new ContactButtonActionListener());
                    } else if (event.equals("get room name")) {
                        clientModel.writeString(clientView.getInput("Enter new room name."));
                        addToRoomView.successMessage();
                    } else if (event.equals("update status view")) {
                        String status = clientModel.getUsernameStatusStream();
                        String username = clientModel.getUsernameStatusStream();
                        if (clientModel.getCurrentRoom().isUserHere(username)) {
                            clientModel.getCurrentRoom().searchUser(username).setStatus(status);
                            clientModel.getUser().setStatus(status);
                            clientView.setStatusImage(username, status);
                        }
                    }
                }
            } catch (Exception e) {
                //System.out.println(socket + "has disconnected.");
                e.printStackTrace();
            }
        }).start();
    }// end of run method

    class SetStatusListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String currStatus = clientModel.getUser().getStatus();
            String username = clientModel.getUser().getUsername();
            statusView = new SettingsView.StatusView();
            statusView.setCurrentStatus(currStatus);
            statusView.online.addActionListener(b -> {
                clientModel.getUser().setStatus("Online");
                clientModel.changeStatus("Online"); //change status in server side
                statusView.setLabelOnline();
            });

            statusView.offline.addActionListener(b2 -> {
                clientModel.getUser().setStatus("Offline");
                clientModel.changeStatus("Offline"); //change status in server side
                statusView.setLabelOffline();
            });

            statusView.afk.addActionListener(b2 -> {
                clientModel.getUser().setStatus("Away from keyboard");
                clientModel.changeStatus("Away from keyboard"); //change status in server side
                statusView.setLabelAFK();
            });

            statusView.busy.addActionListener(b2 -> {
                clientModel.getUser().setStatus("Busy");
                clientModel.changeStatus("Busy"); //change status in server side
                statusView.setLabelBusy();
            });

            statusView.disturb.addActionListener(b2 -> {
                clientModel.getUser().setStatus("Do not disturb");
                clientModel.changeStatus("Do not disturb"); //change status in server side
                statusView.setLabelDisturb();
            });

            statusView.idle.addActionListener(b2 -> {
                clientModel.getUser().setStatus("Idle");
                clientModel.changeStatus("Idle"); //change status in server side
                statusView.setLabelIdle();
            });

            statusView.invi.addActionListener(b2 -> {
                clientModel.getUser().setStatus("Invisible");
                clientModel.changeStatus("Invisible"); //change status in server side
                statusView.setLabelInvi();
            });
        }
    }

    class MembersSearchTextListener implements TextListener {
        @Override
        public void textValueChanged(TextEvent e) {
            TextField tf = (TextField) e.getSource();
            String username = tf.getText();
            if (!username.equals("")) {
                clientView.changeMemberButtonPanel(username, clientModel.getCurrentRoom());
            } else
                clientView.originalMemberButtonPanel(clientModel.getCurrentRoom());
            clientView.setAddItemActionListener(new AddContactListener());
        }
    }

    class ContactsSearchListener implements TextListener {
        @Override
        public void textValueChanged(TextEvent e) {
            TextField tf = (TextField) e.getSource();
            String username = tf.getText();
            if (!username.equals("")) {
                clientView.changeContactButtons(username, clientModel.getUser());
            } else
                clientView.originalContactButtons();
        }
    }

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
            UserModel newContact = clientModel.getCurrentRoom().searchUser(username);
            clientModel.addContact(username);
            clientModel.getUser().getContacts().add(newContact);

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

    class RemoveBookmarkListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
            JButton invokerButton = (JButton) popupMenu.getInvoker();
            String username = invokerButton.getText();
            clientModel.removeBookmark(username);
        }
    }

    class RemoveContactListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //System.out.println("Inside bookmark listener");
            JMenuItem menuItem = (JMenuItem) e.getSource();
            JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
            JButton invokerButton = (JButton) popupMenu.getInvoker();
            String username = invokerButton.getText();
            System.out.println("remove " + username);
            clientModel.removeContact(username);
        }
    }

    class LogOutListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            clientModel.logout();
            clientView.dispose();
            new LoginController(socket, outputStream, inputStream).run();
        }
    }
}// END OF CLIENT CONTROLLER
