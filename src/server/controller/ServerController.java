package server.controller;

import client.controller.ClientController;
import server.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * run server controller
 * run the server
 * read the list of users
 * wait for validate users
 *
 */
public class ServerController {
    public static ArrayList<UserModel> users;
    public ServerModel serverModel;
    private static final int port = 2022;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static ExecutorService pool = Executors.newCachedThreadPool();

    public ServerController() {
        serverModel = new ServerModel(Utility.readData("res/data.dat"), new ArrayList<>());
    }

    public void run() {
        ArrayList<ClientController> clients = new ArrayList<>();
        serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server Started");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // Accept client connection
            clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket);
            pool = Executors.newCachedThreadPool();
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            //ClientController clientThread = new ClientController(clientSocket);
            AuthenticatorModel authenticate;
            Object input;
            while (true) {
                input = inputStream.readObject();
                if (input.equals("login")) {
                    authenticate = new AuthenticatorModel(inputStream, outputStream, serverModel.getRegisteredUsers());
                    String username = (String) inputStream.readObject();
                    String password = (String) inputStream.readObject();
                    System.out.printf("Attempting to login with username:%s and password:%s\n", username, password);
                    if (authenticate.verifyUser(username, password)) {
                        ClientHandlerModel client = new ClientHandlerModel(clientSocket, inputStream, outputStream, serverModel.getChatHistory());
                        //clients.add(clientThread);
                        pool.execute(client);
                    }
                } else if (input.equals("register")) {
                    System.out.println("Attempting to register.");
                    input = inputStream.readObject();
                    serverModel.addRegisteredUser((UserModel) input);
                    Utility.exportData(serverModel.getRegisteredUsers());
                    outputStream.writeObject("registered");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
