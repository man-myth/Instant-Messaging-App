package client.controller;

import client.view.ClientView;
import server.model.UserModel;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientController implements Runnable {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private UserModel user;
    ClientView clientView;

    //list of connected clients
    public static ArrayList<ClientController> clientHandlerControllers = new ArrayList<>();


    public ClientController(ObjectInputStream inputStream, ObjectOutputStream outputStream, UserModel user) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.user = user;
    }

    @Override
    public void run() {
        clientView = new ClientView();
        clientView.setVisible(true);

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

    }
}
