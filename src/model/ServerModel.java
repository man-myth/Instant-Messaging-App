package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerModel {
    private static int port = 2015;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static ExecutorService pool = Executors.newCachedThreadPool();

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

    public static void main(String[] args) throws IOException {

        int port = 2022;
        ArrayList<ClientHandler> clients = new ArrayList<>();
        ServerSocket serverSocket = new ServerSocket(port);
        ExecutorService pool = Executors.newCachedThreadPool();


        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientThread = new ClientHandler(clientSocket);

            clients.add(clientThread);
            pool.execute(clientThread);
        }
    }
}
