package server.controller;

import client.controller.ClientController;
import client.controller.LoginController;
import client.view.AddContactToRoomView;
import client.view.ExitOnCloseAdapter;
import client.view.KickContactFromRoomView;
import client.view.SettingsView;
import common.ChatRoomModel;
import common.MessageModel;
import common.UserModel;
import server.model.AdminModel;
import server.view.AdminView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;


public class AdminController implements Runnable {
    // -Fields
    private final Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
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
    private UserModel user;

    // -Constructor


    public AdminController(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
                           UserModel user, ChatRoomModel publicChat) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.currentRoom = publicChat;
        this.adminModel = new AdminModel(socket, inputStream, outputStream, user, publicChat);
    }

    // -Methods

    // run method when calling admin controller
    public void run() {
        System.out.println("Logged in with user: " + adminModel.getUser());
        adminView = new AdminView(adminModel.getUser(), adminModel.getCurrentRoom());
        adminView.setWindowAdapter(new ExitOnCloseAdapter(socket));
        adminView.setStatusImage(adminModel.getUser().getUsername(), adminModel.getUser().getStatus());
        adminModel.changeStatus("Online");
        adminModel.readAllStatus();

        //settings listener
        adminView.settingsButtonListener(new SettingsListener());

        //adding of contact to a room listener
        adminView.setAddButtonActionListener(new AddToRoomButtonListener());

        //kick user from the room listener
        adminView.setKickButtonActionListener(new KickButtonListener());

        //broadcasting messages listener
        adminView.setMessageListener(new MessageListener());

        //member button popup menu listener
        adminView.setAddItemActionListener(new AddContactListener());

        //contact buttons listener
        adminView.setContactButtonsActionListener(new ContactButtonActionListener());

        //members search bar text listener
        adminView.membersSearchActionListener(new MembersSearchTextListener());

        //contacts search bar text listener
        adminView.contactsSearchListener(new ContactsSearchListener());

        //bookmark button popup menu listener
        adminView.setBookmarkButtonActionListener(new AddBookmarkListener());

        //remove bookmark button popup menu listener
        adminView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());

        //bookmark contact popup menu listener
        adminView.setRemoveContactButtonActionListener(new RemoveContactListener());

        //logout listener
        adminView.setLogOutListener(new LogOutListener());

        // Separate thread for GUI
        EventQueue.invokeLater(() -> adminView.setVisible(true));

        // Thread for receiving responses from the server
        new Thread(() -> {
            try {
                while (adminModel.isLoggedIn()) {
                    String event = adminModel.getEvent();
                    System.out.println("Event: " + event);
                    if (event.equals("broadcast")) { // do this if event = "broadcast"
                        MessageModel message = adminModel.getMessageFromStream();
                        if (adminModel.getCurrentRoom().getName().equalsIgnoreCase("Public Chat")) {
                            adminView.addMessage(message);
                        }

                    } else if (event.equals("contact added")) { // do this if event = "contact added"
                        adminModel.updateChatRooms();
                        adminModel.updateContacts();
                        adminView.updateContacts(adminModel.getUser());

                        adminView.setContactButtonsActionListener(new ContactButtonActionListener());
                        adminView.setBookmarkButtonActionListener(new AddBookmarkListener());
                        adminView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());
                        adminView.setRemoveContactButtonActionListener(new RemoveContactListener());
                        adminView.contactsSearchListener(new ContactsSearchListener());

                    } else if (event.equals("adding self")) {
                        adminView.showErrorMessage("You are adding yourself!");
                    } else if (event.equals("contact updated")) { // do this if event = "contact added/removed"
                        adminModel.updateUser();
                        adminView.updateContacts(adminModel.getUser());

                        // Re-set action listeners
                        adminView.setContactButtonsActionListener(new ContactButtonActionListener());
                        adminView.setBookmarkButtonActionListener(new AddBookmarkListener());
                        adminView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());
                        adminView.setRemoveContactButtonActionListener(new RemoveContactListener());
                        adminView.contactsSearchListener(new ContactsSearchListener());
                    } else if (event.equals("bookmark updated")) { // do this if event = "bookmark added/removed"
                        adminModel.updateUser();
                        System.out.println("updating contacts....");
                        adminView.updateContacts(adminModel.getUser());
                        // Re-set action listeners
                        adminView.setContactButtonsActionListener(new ContactButtonActionListener());
                        adminView.setBookmarkButtonActionListener(new AddBookmarkListener());
                        adminView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());
                        adminView.setRemoveContactButtonActionListener(new RemoveContactListener());
                        adminView.contactsSearchListener(new ContactsSearchListener());
                    } else if (event.equals("return room")) {
                        adminModel.receiveRoom();
                        adminView.updateRoom(adminModel.getCurrentRoom());

                        // Re-set action listeners
                        adminView.setAddItemActionListener(new AddContactListener());
                        adminView.setMessageListener(new MessageListener());
                    } else if (event.equals("new message")) {
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
                    } else if (event.equals("update status view")) {
                        String status = adminModel.getUsernameStatusStream();
                        String username = adminModel.getUsernameStatusStream();
                        if (adminModel.getCurrentRoom().isUserHere(username)) {
                            adminModel.getCurrentRoom().searchUser(username).setStatus(status);
                            adminModel.getUser().setStatus(status);
                            adminView.setStatusImage(username, status);
                        }
                    }
                }
            } catch (Exception e) {
                //System.out.println(socket + "has disconnected.");
                e.printStackTrace();
            }
        }).start();

    }//end of run() thread

    class SettingsListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //asking new username listener
            settingsView = new SettingsView();
            settingsView.changeNameActionListener(e1 -> adminView.promptErrorChangeUser());

            //asking new password listener
            settingsView.changePassActionListener(e2 -> {
                newPass = new SettingsView.AskNewPass(); // access the AskNewPass class from SettingsView
                newPass.changeListener(f -> { // action listener for the button in AskNewPass
                    String enteredPass = newPass.getPass();
                    String reEnteredPass = newPass.getRePass();
                    boolean isPassValid = adminModel.isPassValid(enteredPass, reEnteredPass); // checks if passwords match
                    newPass.promptError(isPassValid); // prompt an error if passwords do not match
                    adminModel.changePassword(enteredPass, isPassValid); // else, change password
                    newPass.changeSuccess(isPassValid);
                });
            });


            //set status listener
            settingsView.changeStatusActionListener(new SetStatusListener());

            //help module display
            settingsView.helpActionListener(e3 -> {
                helpModule = new SettingsView.HelpModule(); // access the HelpModule class from SettingsView

            });
        }
    }

    class SetStatusListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String currStatus = adminModel.getUser().getStatus();
            String username = adminModel.getUser().getUsername();
            statusView = new SettingsView.StatusView();
            statusView.setCurrentStatus(currStatus);
            statusView.online.addActionListener(b -> {
                adminModel.getUser().setStatus("Online");
                adminModel.changeStatus("Online"); //change status in server side
                statusView.setLabelOnline();
            });

            statusView.offline.addActionListener(b2 -> {
                adminModel.getUser().setStatus("Offline");
                adminModel.changeStatus("Offline"); //change status in server side
                statusView.setLabelOffline();
            });

            statusView.afk.addActionListener(b2 -> {
                adminModel.getUser().setStatus("Away from keyboard");
                adminModel.changeStatus("Away from keyboard"); //change status in server side
                statusView.setLabelAFK();
            });

            statusView.busy.addActionListener(b2 -> {
                adminModel.getUser().setStatus("Busy");
                adminModel.changeStatus("Busy"); //change status in server side
                statusView.setLabelBusy();
            });

            statusView.disturb.addActionListener(b2 -> {
                adminModel.getUser().setStatus("Do not disturb");
                adminModel.changeStatus("Do not disturb"); //change status in server side
                statusView.setLabelDisturb();
            });

            statusView.idle.addActionListener(b2 -> {
                adminModel.getUser().setStatus("Idle");
                adminModel.changeStatus("Idle"); //change status in server side
                statusView.setLabelIdle();
            });

            statusView.invi.addActionListener(b2 -> {
                adminModel.getUser().setStatus("Invisible");
                adminModel.changeStatus("Invisible"); //change status in server side
                statusView.setLabelInvi();
            });
        }
    }

    class AddToRoomButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
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
                    boolean isUserHere = adminModel.getCurrentRoom().isUserHere(username);
                    if (isUserHere)
                        addToRoomView.errorUserIsHere();
                    else {
                        adminModel.addContactToRoom(newMember, adminModel.getCurrentRoom().getName());
                        if (!adminModel.getCurrentRoom().getAdmin().equals("")) {
                            addToRoomView.successMessage();
                        }
                    }
                } catch (NullPointerException error) {
                    addToRoomView.errorInvalidAction();
                }
            });
        }
    }

    class KickButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] contactArray = adminModel.listToStringArrayAdd(adminModel.getCurrentRoom().getUsers());
            kickUserView = new KickContactFromRoomView(contactArray);

            kickUserView.setKickButtonActionListener(e1 -> {
                try {
                    if (adminModel.isAdmin(adminModel.getUser())) {
                        String username = kickUserView.getSelected();
                        UserModel roomMember = adminModel.getCurrentRoom().searchUser(username);
                        adminModel.getCurrentRoom().kickUser(roomMember);
                        adminView.kickMember(roomMember);
                        kickUserView.successMessage();
                    } else {
                        adminView.noPermsMsg();
                    }

                } catch (NullPointerException error) {
                    kickUserView.errorInvalidAction();
                }
            });
        }
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
}// END OF ADMIN CONTROLLER

