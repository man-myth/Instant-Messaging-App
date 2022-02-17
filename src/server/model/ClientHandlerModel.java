package server.model;

import client.controller.ClientController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandlerModel implements Runnable {
    private final Socket clientSocket;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;

    public ClientHandlerModel(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());

            AuthenticatorModel authenticate;
            Object input;
            while (true) {
                try {
                    input = inputStream.readObject();
                    if (input.equals("login")) {
                        authenticate = new AuthenticatorModel(inputStream, outputStream, ServerModel.getRegisteredUsers());
                        String username = (String) inputStream.readObject();
                        String password = (String) inputStream.readObject();
                        System.out.printf("Attempting to login with username:%s and password:%s\n", username, password);
                        if (authenticate.verifyUser(username, password)) {
                            outputStream.writeObject("VERIFIED");
                            ClientController clientThread = new ClientController(inputStream, outputStream, new UserModel(username, password));
                        }
                    } else if (input.equals("register")) {
                        System.out.println("Attempting to register.");
                        input = inputStream.readObject();
                        ServerModel.addRegisteredUser((UserModel) input);
                        Utility.exportData(ServerModel.getRegisteredUsers());
                        outputStream.writeObject("registered");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
