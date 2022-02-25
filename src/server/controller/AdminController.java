package server.controller;

import client.controller.ClientController;
import client.controller.LoginController;
import client.view.AddContactToRoomView;
import client.view.ExitOnCloseAdapter;
import client.view.KickContactFromRoomView;
import client.view.SettingsView;
import server.model.AdminModel;
import server.model.ChatRoomModel;
import server.model.MessageModel;
import server.model.UserModel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.ArrayList;
import java.util.List;

import server.view.AdminView;
import server.view.AuthenticatorView;



public class AdminController implements Runnable {
    // -Fields
    private final Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    private UserModel user;

    ChatRoomModel currentRoom;
    AdminView adminView;
    AdminModel adminModel;
    AddContactToRoomView addToRoomView;
    KickContactFromRoomView kickUserView;
    SettingsView settingsView;
    SettingsView.AskNewName newName;
    SettingsView.AskNewPass newPass;
    SettingsView.StatusView statusView;
    SettingsView.HelpModule helpModule;

    // -Constructor


    public AdminController(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
                           UserModel user, ChatRoomModel publicChat) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.currentRoom = publicChat;
        this.adminModel = new AdminModel(socket, inputStream, outputStream,user, publicChat);
    }

    // -Methods

    // run method when calling admin controller
    public void run() {
        System.out.println("Logged in with user: " + adminModel.getUser());
        adminView = new AdminView(adminModel.getUser(), adminModel.getCurrentRoom());
        adminView.setWindowAdapter(new ExitOnCloseAdapter(socket));
        adminView.setStatusImage(adminModel.getUser().getUsername(),adminModel.getUser().getStatus());
        adminModel.changeStatus("Online");
        adminModel.readAllStatus();

        //- settings actions
        adminView.settingsButtonListener(e -> {
            //asking new username listener
            settingsView = new SettingsView();
            settingsView.changeNameActionListener(e1 -> {
                adminView.promptErrorChangeUser(); // admin cannot access the AskNewName class from SettingsView, prompt error

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


            //set status listener
            settingsView.changeStatusActionListener(new SetStatusListener());
                    settingsView.helpActionListener(e3 -> {
                                helpModule = new SettingsView.HelpModule();
                            });
            //help module display
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
        /*
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
         */
        //- broadcasting messages actions
        adminView.setMessageListener(new MessageListener());

        // Set ActionListener for member button popup menu
        adminView.setAddItemActionListener(new AddContactListener());

        // Set ActionListener for contact buttons
        adminView.setContactButtonsActionListener(new ContactButtonActionListener());

        //members search bar text listener
        adminView.membersSearchActionListener(new MembersSearchTextListener());

        //members search bar text listener
        adminView.contactsSearchListener(new ContactsSearchListener());

        // Set ActionListener for contact button popup menu
        adminView.setBookmarkButtonActionListener(new AddBookmarkListener());

        adminView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());

        adminView.setRemoveContactButtonActionListener(new RemoveContactListener());

        adminView.setLogOutListener(new LogOutListener());

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
                        adminModel.updateChatRooms();
                        adminModel.updateContacts();
                        adminView.updateContacts(adminModel.getUser());

                        adminView.setContactButtonsActionListener(new ContactButtonActionListener());
                        adminView.setBookmarkButtonActionListener(new AddBookmarkListener());
                        adminView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());
                        adminView.setRemoveContactButtonActionListener(new RemoveContactListener());
                        adminView.contactsSearchListener(new ContactsSearchListener());
                        // System.out.println(adminModel.getUser().getContacts());

                    } else if (event.equals("adding self")) {
                        adminView.showErrorMessage("You are adding yourself!");
                    } else if (event.equals("contact updated")) { // do this if event = "contact added"
                        adminModel.updateUser();
                        adminView.updateContacts(adminModel.getUser());

                        // Re-set action listeners
                        adminView.setContactButtonsActionListener(new ContactButtonActionListener());
                        adminView.setBookmarkButtonActionListener(new AddBookmarkListener());
                        adminView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());
                        adminView.setRemoveContactButtonActionListener(new RemoveContactListener());
                        adminView.contactsSearchListener(new ContactsSearchListener());
                    }  else if (event.equals("bookmark updated")) { // do this if event = "bookmark added/removed"
                        adminModel.updateUser();
                        System.out.println("updating contacts....");
                        adminView.updateContacts(adminModel.getUser());
                        // Re-set action listeners
                        adminView.setContactButtonsActionListener(new ContactButtonActionListener());
                        adminView.setBookmarkButtonActionListener(new AddBookmarkListener());
                        adminView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());
                        adminView.setRemoveContactButtonActionListener(new RemoveContactListener());
                        adminView.contactsSearchListener(new ContactsSearchListener());
                    }else if (event.equals("return room")) {
                        adminModel.receiveRoom();
                        adminView.updateRoom(adminModel.getCurrentRoom());

                        // Re-set action listeners
                        adminView.setAddItemActionListener(new AddContactListener());
                        adminView.setMessageListener(new MessageListener());
                    }else if (event.equals("new message")) {
                        // Update GUI if current room has new message
                        MessageModel message = adminModel.getMessageFromStream();
                        if (message.getReceiver().getName().equalsIgnoreCase(adminModel.getCurrentRoom().getName())) {
                            adminView.addMessage(message);
                        }
                    } else if (event.equals("update chat rooms")) {
                        adminModel.updateChatRooms();
                        adminView.updateContacts(adminModel.getUser());

                        // Re-set action listeners
                        adminView.setContactButtonsActionListener(new ContactButtonActionListener());
                    } else if (event.equals("get room name")) {
                        adminModel.writeString(adminView.getInput("Enter new room name."));
                        addToRoomView.successMessage();
                    } else if(event.equals("update status view")){
                        String status = adminModel.getUsernameStatusStream();
                        String username = adminModel.getUsernameStatusStream();
                        adminModel.getCurrentRoom().searchUser(username).setStatus(status);
                        adminModel.getUser().setStatus(status);
                        adminView.setStatusImage(username,status);
                    }
                }
            } catch (Exception e) {
                System.out.println(socket + "has disconnected.");
                //e.printStackTrace();
            }
        }).start();

    }//end of run() thread

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

    class MembersSearchTextListener implements TextListener {
        @Override
        public void textValueChanged(TextEvent e) {
            TextField tf = (TextField) e.getSource();
            String username = tf.getText();
            if (!username.equals("")) {
              adminView.changeMemberButtonPanel(username, adminModel.getCurrentRoom());
            } else
               adminView.originalMemberButtonPanel(adminModel.getCurrentRoom());
           adminView.setAddItemActionListener(new AddContactListener());
        }
    }

    class ContactsSearchListener implements TextListener {
        @Override
        public void textValueChanged(TextEvent e) {
            TextField tf = (TextField) e.getSource();
            String username = tf.getText();
            if (!username.equals("")) {
                adminView.changeContactButtons(username, adminModel.getUser());
            } else
               adminView.originalContactButtons();
        }
    }

    class ContactButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String room = ((JButton) e.getSource()).getText();
            if (room.equals(adminModel.getCurrentRoom().getName())) {
                return;
            }
            adminModel.requestRoom(room);
        }
    }

    class MessageListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String message = adminView.getMessage();
            ChatRoomModel currentRoom = adminModel.getCurrentRoom();
            MessageModel msg = new MessageModel(adminModel.getUser(), currentRoom, message, LocalTime.now(),
                    LocalDate.now());

            // Public chat
            if (currentRoom.getName().equalsIgnoreCase("Public Chat")) {
                boolean doBroadcast = adminModel.broadcastMessage(msg);
                if (doBroadcast) {
                    adminView.addMessage(msg);
                    adminView.clearTextArea();
                }
                // Conference or Private Message
            } else {
                adminModel.sendMessage(msg);
                adminView.addMessage(msg);
                adminView.clearTextArea();
            }
        }
    }
    class AddContactListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
            JButton invokerButton = (JButton) popupMenu.getInvoker();
            String username = invokerButton.getText();
            UserModel newContact = adminModel.getCurrentRoom().searchUser(username);
            adminModel.addContact(username);
            adminModel.getUser().getContacts().add(newContact);

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
            adminModel.addBookmark(username);
        }
    }

    class RemoveBookmarkListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
            JButton invokerButton = (JButton) popupMenu.getInvoker();
            String username = invokerButton.getText();
            adminModel.removeBookmark(username);
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
            adminModel.removeContact(username);
        }
    }

    class LogOutListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            adminModel.logout();
            adminView.dispose();
            new LoginController(socket, outputStream, inputStream).run();
        }
    }
    class SetStatusListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String currStatus = adminModel.getUser().getStatus();
            statusView = new SettingsView.StatusView();
            statusView.setCurrentStatus(currStatus);
            statusView.online.addActionListener(b -> {
                adminModel.getUser().setStatus("Online");
                adminModel.changeStatus("Online"); // change status in server side
                statusView.setLabelOnline();
            });

            statusView.offline.addActionListener(b2 -> {
                adminModel.getUser().setStatus("Offline");
                adminModel.changeStatus("Offline"); // change status in server side
                statusView.setLabelOffline();
            });

            statusView.afk.addActionListener(b2 -> {
                adminModel.getUser().setStatus("Away from keyboard");
                adminModel.changeStatus("Away from keyboard"); // change status in server side
                statusView.setLabelAFK();
            });

            statusView.busy.addActionListener(b2 -> {
                adminModel.getUser().setStatus("Busy");
                adminModel.changeStatus("Busy"); // change status in server side
                statusView.setLabelBusy();
            });

            statusView.disturb.addActionListener(b2 -> {
                adminModel.getUser().setStatus("Do not disturb");
                adminModel.changeStatus("Do not disturb"); // change status in server side
                statusView.setLabelDisturb();
            });

            statusView.idle.addActionListener(b2 -> {
                adminModel.getUser().setStatus("Idle");
                adminModel.changeStatus("Idle"); // change status in server side
                statusView.setLabelIdle();
            });

            statusView.invi.addActionListener(b2 -> {
                adminModel.getUser().setStatus("Invisible");
                adminModel.changeStatus("Invisible"); // change status in server side
                statusView.setLabelInvi();
            });
        }
    }
}// END OF ADMIN CONTROLLER

