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


public class AdminController {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private UserModel admin;
    ChatRoomModel currentRoom;
    AdminView adminView;
    AdminModel adminModel;


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
        adminView.setAddButtonActionListener(e -> {
            List<UserModel> contacts = new ArrayList<>();
            contacts.add(new UserModel("testing", "123"));
            contacts.add(new UserModel("testing1", "123"));
            contacts.add(new UserModel("testing2", "123"));

            //String[] contactUsernames = clientModel.getUser().getContacts().stream().map(user -> user.getUsername()).toArray(String[]::new);
            String[] contactUsernames = contacts.stream().map(user -> user.getUsername()).toArray(String[]::new);
            AddContactToRoomView addContactView = new AddContactToRoomView(contactUsernames);

            addContactView.setAddButtonActionListener(e1 -> {
                try {
                    outputStream.writeObject("add contact to room");
                    String username = addContactView.getSelected();
                    outputStream.writeObject(username);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            });

            adminView.setKickButtonActionListener(e -> {
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
        adminView.setMessageListener(e -> {
            broadcastMessage();
        });
        EventQueue.invokeLater(() -> adminView.setVisible(true));

        new Thread(() -> {
            Object msg;
            while (true) {
                try {
                    msg = inputStream.readObject();
                    if (msg.equals("broadcast")) {
                        receiveMessage((MessageModel) inputStream.readObject());
                    } else if (msg.equals("return contacts")) {

                    } else if (msg.equals("done adding contact")) {

                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("User Disconnected");
                }
            }
        }).start();

    }

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
