package client.model;

import client.controller.LoginController;
import server.model.MessageModel;
import server.model.UserModel;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ClientModel implements Runnable {

    final private Socket clientSocket;
    private ObjectInputStream inputStream;
    final private ObjectOutputStream outputStream;
    UserModel user;

    public ClientModel(Socket clientSocket, ObjectInputStream inputStream, ObjectOutputStream outputStream, UserModel user) {
        this.clientSocket = clientSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.user = user;
    }

    @Override
    public void run() {
        //MessageModel messageReceived;
        Object msg;
        System.out.println("hi!");

        while (true) {
            try {
                // receive the string
                //messageReceived = (MessageModel) inputStream.readObject();

                //chatHistory.add(messageReceived);

                //System.out.println(messageReceived.getSender() + ": " + messageReceived.getContent());

//                if(messageReceived.equals("BYE")){  //replace with the method to exit
//                    this.clientSocket.close();
//                    break;
//                }
                msg = inputStream.readObject();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
