package server.model;

import client.controller.ClientController;
import common.UserModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerModel {
    private static final int port = 2015;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    List<UserModel> registeredUsers;
    List<MessageModel> chatHistory;

    private String checkStatus(String username) {
        return "";
    }

    private boolean checkUserType(String username) {
        return false;
    }

    private MessageModel searchConvo(String key) {
        return null;
    }

    private void createChat(String name) {

    }

    public ServerModel(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void run(){

        int port = 2019;
        ArrayList<ClientController> clients = new ArrayList<>();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            ExecutorService pool = Executors.newCachedThreadPool();


            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientController clientThread = new ClientController(clientSocket);

                clients.add(clientThread);
                pool.execute(clientThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
