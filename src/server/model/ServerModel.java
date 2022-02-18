package server.model;

import client.controller.ClientController;
import client.model.ClientModel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerModel {
    private static final int PORT = 2022;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static ExecutorService pool;

    static List<UserModel> registeredUsers;
    static ChatRoomModel publicChat;
    List<ClientHandlerModel> clients;

    public ServerModel(List<UserModel> registeredUsers, ChatRoomModel publicChat) {
        this.registeredUsers = registeredUsers;
        this.publicChat = publicChat;
        for (UserModel user : registeredUsers) {
            System.out.println(user);
        }
    }

    public void run() {
        clients = new ArrayList<>();
        serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            serverSocket.setReuseAddress(true);
            pool = Executors.newFixedThreadPool(2);
            System.out.println("[SERVER]: Started.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                // Accept client connection
                clientSocket = serverSocket.accept();
                System.out.println("[SERVER]: Client connected: " + clientSocket);
                ClientHandlerModel clientHandler = new ClientHandlerModel(clientSocket);
                clients.add(clientHandler);
                pool.execute(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
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

    public static List<UserModel> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(List<UserModel> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public static void addRegisteredUser(UserModel user) {
        registeredUsers.add(user);
    }

    public static ChatRoomModel getPublicChat() {
        return publicChat;
    }

    public static void setPublicChat(ChatRoomModel newPublicChat) {
        publicChat = newPublicChat;
    }

    public List<ClientHandlerModel> getClients() {
        return clients;
    }

    public void setClients(List<ClientHandlerModel> clients) {
        this.clients = clients;
    }
}
