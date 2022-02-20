package server.model;

import client.controller.LoginController;
import server.model.MessageModel;
import server.model.UserModel;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class AdminModel implements Runnable {

    final private Socket clientSocket;
    private ObjectInputStream inputStream;
    final private ObjectOutputStream outputStream;
    UserModel user;

    public AdminModel(Socket clientSocket, ObjectInputStream inputStream, ObjectOutputStream outputStream, UserModel user) {
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
                msg = inputStream.readObject();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public UserModel getUser() {
        return this.user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
