package server.model;

import client.controller.ClientController;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerModel {
    List<UserModel> registeredUsers;
    List<MessageModel> chatHistory;
    List<ClientController> clients;
    private static final int port = 2022;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static ExecutorService pool;

    public ServerModel(List<UserModel> registeredUsers, List<MessageModel> chatHistory) {
        this.registeredUsers = registeredUsers;
        this.chatHistory = chatHistory;
        pool = Executors.newFixedThreadPool(2);
    }

    public void run() {
        clients = new ArrayList<>();
        serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server Started");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            ObjectOutputStream outputStream = null;
            ObjectInputStream inputStream = null;
            try {
                // Accept client connection
                clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //ClientController clientThread = new ClientController(clientSocket);
            AuthenticatorModel authenticate;
            Object input;
            while (true) {
                try {
                    input = inputStream.readObject();
                    if (input.equals("login")) {
                        authenticate = new AuthenticatorModel(inputStream, outputStream, getRegisteredUsers());
                        String username = (String) inputStream.readObject();
                        String password = (String) inputStream.readObject();
                        System.out.printf("Attempting to login with username:%s and password:%s\n", username, password);
                        if (authenticate.verifyUser(username, password)) {
                            ClientHandlerModel client = new ClientHandlerModel(clientSocket, inputStream, outputStream, getChatHistory());
                            //clients.add(clientThread);
                            pool.execute(client);
                        }
                    } else if (input.equals("register")) {
                        System.out.println("Attempting to register.");
                        input = inputStream.readObject();
                        addRegisteredUser((UserModel) input);
                        Utility.exportData(getRegisteredUsers());
                        outputStream.writeObject("registered");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private String checkStatus(String username) {
        return "";
    }

    private boolean checkUserType(String username) {
        return false;
    }

    private MessageModel searchConvo(String key) {
        return null;
    }

    public void createChat(String name) {

    }

    public List<UserModel> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(List<UserModel> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public List<MessageModel> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<MessageModel> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public void addRegisteredUser(UserModel user) {
        this.registeredUsers.add(user);
    }


}
