package client.controller;

import client.view.ClientView;
import client.view.ExitOnCloseAdapter;
import server.model.ChatRoomModel;
import server.model.MessageModel;
import server.model.UserModel;

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
    private UserModel user;
    ChatRoomModel currentRoom;
    ClientView clientView;

    public ClientController(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
            UserModel user, ChatRoomModel publicChat) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.user = user;
        this.currentRoom = publicChat;
    }

    public void run() {
        System.out.println("Logged in with user: " + user);
        clientView = new ClientView(user, currentRoom);
        clientView.setWindowAdapter(new ExitOnCloseAdapter(socket));
        clientView.actionListenerAdd(e -> {
            AddContactToRoomController addContact = new AddContactToRoomController(inputStream, outputStream);
        });
        clientView.setMessageListener(e -> {
            String message = clientView.getMessage();
            if (message.isEmpty()) {
                return;
            }
            try {
                outputStream.writeObject("broadcast");
                MessageModel msg = new MessageModel(user, currentRoom, message, LocalTime.now(), LocalDate.now());
                outputStream.writeObject(msg);
                clientView.addMessage(msg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            clientView.clearTextArea();
        });
        clientView.setVisible(true);
    }
}
