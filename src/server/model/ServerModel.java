package server.model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerModel {
    private static final int port = 2022;
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

    private void addUser(UserModel user){
        registeredUsers.add(user);
    }

    public void run() {
        //ArrayList<ClientController> clients = new ArrayList<>();
        registeredUsers = Utility.readData("res/data.dat");

        serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server Started");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Client connected: ." + clientSocket);
                ExecutorService pool = Executors.newCachedThreadPool();
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                //ClientController clientThread = new ClientController(clientSocket);
                AuthenticatorModel authenticate = new AuthenticatorModel(inputStream, outputStream, registeredUsers);
                if (authenticate.verifyUser()) {
                    ClientHandlerModel client = new ClientHandlerModel(clientSocket, inputStream, outputStream, chatHistory);
                    //clients.add(clientThread);
                    pool.execute(client);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
