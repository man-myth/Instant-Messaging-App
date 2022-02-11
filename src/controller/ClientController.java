package controller;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientController implements Runnable{
    private Socket client;
    private BufferedReader reader;
    private PrintWriter writer;

    //list of connected clients
    public static ArrayList<ClientController> clientHandlerControllers = new ArrayList<>();

    /**
     * Creates
     * @param clientSocket
     */
    public ClientController(Socket clientSocket){
            try {
                this.client = clientSocket;
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer = new PrintWriter(client.getOutputStream(),true);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

    @Override
    public void run(){
        //TODO what the server will do when called

    }
}
