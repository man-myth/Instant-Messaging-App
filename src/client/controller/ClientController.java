package client.controller;

import client.model.ClientModel;
import client.view.*;
import common.ChatRoomModel;
import common.MessageModel;
import common.UserModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
                            UserModel user, ChatRoomModel room) {
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.currentRoom = room;
        this.clientModel = new ClientModel(socket, inputStream, outputStream, user, room);

    }

    // -Methods

    // run method when calling client controller
    public void run() {
        System.out.println("Logged in with user: " + clientModel.getUser());
        clientView = new ClientView(clientModel.getUser(), clientModel.getCurrentRoom());
        System.out.println("++++" + clientModel.getCurrentRoom().searchUser("user2").getStatus());
        clientView.setWindowAdapter(new ExitOnCloseAdapter(socket));
        clientView.setStatusImage(clientModel.getUser().getUsername(), clientModel.getUser().getStatus());
        clientModel.changeStatus("Online");
        clientModel.readAllStatus();

        //settings listener
        clientView.settingsButtonListener(new SettingsListeners());

        //adding of contact to a room listener
        clientView.setAddButtonActionListener(new AddToRoomListener());

        //removing a user from app
        clientView.setRemoveUserActionListener(new SuspendUserListener());

        //reactivating account
        clientView.setReactivateUserActionListener(new ReactivateUserListener());

        //kick user from the room listener
        clientView.setKickButtonActionListener(new KickFromRoomListener());

        //broadcasting messages listener
        clientView.setMessageListener(new MessageListener());

        //add button popup menu listener
        clientView.setAddItemActionListener(new AddContactListener());

        //contact buttons listener
        clientView.setContactButtonsActionListener(new ContactButtonActionListener());

        //members search bar text listener
        clientView.membersSearchActionListener(new MembersSearchTextListener());

        //contacts search bar text listener
        clientView.contactsSearchListener(new ContactsSearchListener());

        //add bookmark listener
        clientView.setBookmarkButtonActionListener(new AddBookmarkListener());

        //remove bookmark listener
        clientView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());

        //remove contact listener
        clientView.setRemoveContactButtonActionListener(new RemoveContactListener());

        //logout listener
        clientView.setLogOutListener(new LogOutListener());

        // Separate thread for GUI
        EventQueue.invokeLater(() -> clientView.setVisible(true));

        // Thread for receiving responses from the server
        new Thread(() -> {
            try {
                while (clientModel.isLoggedIn()) {
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
                    } else if (event.equals("already has contact")){
                        clientView.showErrorMessage("You already have a contact of that user");
                    }else if (event.equals("contact updated")) { // do this if event = "contact added/removed"
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
                        clientModel.updateUser();
                        clientView.updateRoom(clientModel.getCurrentRoom());

                        if (clientModel.getUser().roomHasUnreadMessage(clientModel.getCurrentRoom().getName())) {
                            clientModel.getUser().clearUnreadMessagesFromRoom(clientModel.getCurrentRoom().getName());
                            clientView.updateContacts(clientModel.getUser());

                            // Re-set action listeners
                            clientView.setContactButtonsActionListener(new ContactButtonActionListener());
                            clientView.setBookmarkButtonActionListener(new AddBookmarkListener());
                            clientView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());
                            clientView.setRemoveContactButtonActionListener(new RemoveContactListener());
                            clientView.contactsSearchListener(new ContactsSearchListener());
                        }

                        // Re-set action listeners
                        clientView.setAddItemActionListener(new AddContactListener());
                        clientView.setRemoveUserActionListener(new SuspendUserListener());
                        clientView.setReactivateUserActionListener(new ReactivateUserListener());
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
                        clientView.updateRoom(clientModel.getCurrentRoom());

                        // Re-set action listeners
                        clientView.setContactButtonsActionListener(new ContactButtonActionListener());
                        clientView.setBookmarkButtonActionListener(new AddBookmarkListener());
                        clientView.setRemoveBookmarkButtonActionListener(new RemoveBookmarkListener());
                        clientView.setRemoveContactButtonActionListener(new RemoveContactListener());
                        clientView.setAddItemActionListener(new AddContactListener());
                        clientView.setMessageListener(new MessageListener());
                        clientView.contactsSearchListener(new ContactsSearchListener());
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
                    } else if (event.equals("suspended user")) {
                        String status = clientModel.getUsernameStatusStream();
                        String username = clientModel.getUsernameStatusStream();
                        System.out.println("inside update status view : " + username + status);
//                          if(clientModel.getUser().getStatus().equals("Suspended")){
//                            new LogOutListener().logout();
//                        }
                        clientView.setStatusImage(username, status);
//                        if (clientModel.getCurrentRoom().isUserHere(username)) {
//                            clientModel.getCurrentRoom().searchUser(username).setStatus(status);
//                            //clientModel.getUser().setStatus(status);
//                            clientView.setStatusImage(username, status);
//                        }
                    }
                }
            } catch (Exception e) {
                //System.out.println(socket + "has disconnected.");
                e.printStackTrace();
            }
        }).start();
    }// end of run method


/*---ACTION LISTENERS---*/

    class SettingsListeners implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //asking new username listener
            settingsView = new SettingsView();
            settingsView.changeNameActionListener(e1 -> {
                if (clientModel.getUser().getUsername().equals("admin")){
                    clientView.showErrorMessage("Admin's username is fixed.");
                }else {
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
                }
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

        }
    }

    class AddToRoomListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
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
        }
    }


    class KickFromRoomListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] contactArray = clientModel.listToStringArrayAdd(clientModel.getCurrentRoom().getUsers());
            kickUserView = new KickContactFromRoomView(contactArray);

            kickUserView.setKickButtonActionListener(e1 -> {
                try {
                    if (clientModel.isAdmin(clientModel.getUser())) {
                        String username = kickUserView.getSelected();
                        clientModel.kickContactFromRoom(username, clientModel.getCurrentRoom().getName());
                        kickUserView.successMessage();
                    } else {
                        clientView.noPermsMsg();
                    }

                } catch (NullPointerException error) {
                    kickUserView.errorInvalidAction();
                }
            });
        }
    }

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
            clientModel.addContact(username);
            //changes: removed duplicate add
        }
    }
    class SuspendUserListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
            JButton invokerButton = (JButton) popupMenu.getInvoker();
            String username = invokerButton.getText();
            System.out.println("suspend " + username);
            clientModel.suspendUser(username);
        }
    }
    class ReactivateUserListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent();
            JButton invokerButton = (JButton) popupMenu.getInvoker();
            String username = invokerButton.getText();
            System.out.println("suspend " + username);
            clientModel.reactivateUser(username);
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


            if(clientModel.isBookmarked(username)) {
                clientView.showErrorMessage("Please 'remove bookmark' first before removing contact.");
            } else
            clientModel.removeContact(username);
        }
    }

    class LogOutListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
           logout();
        }

        public void logout(){
            clientModel.logout();
            clientView.dispose();
            new LoginController(socket, outputStream, inputStream).run();
        }
    }
}// END OF CLIENT CONTROLLER
