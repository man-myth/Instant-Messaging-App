package client.controller;

import client.model.ClientModel;
import client.view.ClientView;
import client.view.ExitOnCloseAdapter;
import server.model.ChatRoomModel;
import server.model.MessageModel;
import server.model.UserModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ClientController {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    ChatRoomModel currentRoom;
    ClientView clientView;
    ClientModel clientModel;

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
        clientView.actionListenerAdd(e -> {
            AddContactToRoomController addContact = new AddContactToRoomController(inputStream, outputStream);
        });
        clientView.setMessageListener(e -> {
            addMessage();
        });
        EventQueue.invokeLater(() -> clientView.setVisible(true));
        //clientModel.run();
        new Thread(() -> {
            Object msg;
            System.out.println("hi!");

            while (true) {
                try {
                    msg = inputStream.readObject();
                    System.out.println(msg);
                    if (msg.equals("broadcast")) {
                        receiveMessage((MessageModel) inputStream.readObject());
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

    public void addMessage() {
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
