package client.controller;

import client.model.ClientModel;
import client.view.AddContactToRoomView;
import client.view.ClientView;
import client.view.ExitOnCloseAdapter;
import server.model.ChatRoomModel;
import server.model.MessageModel;
import server.model.UserModel;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class ClientController {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    ChatRoomModel currentRoom;
    ClientView clientView;
    ClientModel clientModel;

    // Changes: Moved code from AddContactToRoomController to run method

    public ClientController(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
            UserModel user, ChatRoomModel publicChat) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.currentRoom = publicChat;
        this.clientModel = new ClientModel(socket, inputStream, outputStream, user);
    }

    public void run() {
        System.out.println("Logged in with user: " + clientModel.getUser());
        clientView = new ClientView(clientModel.getUser(), currentRoom);
        clientView.setWindowAdapter(new ExitOnCloseAdapter(socket));
        clientView.setAddButtonActionListener(e -> {
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

        });
        clientView.setMessageListener(e -> {
            broadcastMessage();
        });
        EventQueue.invokeLater(() -> clientView.setVisible(true));

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
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void receiveMessage(MessageModel message) {
        clientView.addMessage(message);
    }

    public void broadcastMessage() {
        String message = clientView.getMessage();
        if (message.isEmpty()) {
            return;
        }
        try {
            outputStream.writeObject("broadcast");
            MessageModel msg = new MessageModel(clientModel.getUser(), currentRoom, message, LocalTime.now(), LocalDate.now());
            outputStream.writeObject(msg);
            clientView.addMessage(msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        clientView.clearTextArea();
    }
}
