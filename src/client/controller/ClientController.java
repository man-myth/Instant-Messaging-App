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

    public ClientController(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream, UserModel user, ChatRoomModel publicChat) {
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
        clientView.setMessageListener(e -> {
            String message = clientView.getMessage();
            if (message.isEmpty()) {
                return;
            }
            try {
                outputStream.writeObject("chat");
                outputStream.writeObject(new MessageModel(user, currentRoom, message, LocalTime.now(), LocalDate.now()));
                clientView.updateChatBox((ChatRoomModel) inputStream.readObject());
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            clientView.clearTextArea();
        });
        clientView.setVisible(true);
    }
}
