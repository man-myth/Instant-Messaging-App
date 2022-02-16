package client.controller;

import server.model.UserModel;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientController implements Runnable {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private UserModel user;

    //list of connected clients
    public static ArrayList<ClientController> clientHandlerControllers = new ArrayList<>();

    public ClientController() {
    }

    @Override
    public void run() {
        int port = 2022;
        try {
            socket = new Socket("localhost", port);
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            LoginController loginController = new LoginController(inputStream, outputStream);

                /*
                System.out.println("----------Connection Succesful-------------");
                System.out.println("Write message below");
                String msg = keyboard.nextLine();
                try {
                    // write on the output stream
                    outputStream.writeObject(msg);
                    System.out.println("message sent " + msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                 */

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
