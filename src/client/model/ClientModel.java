package client.model;

import client.controller.LoginController;
import server.model.MessageModel;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ClientModel implements Runnable {

    final private Socket clientSocket;
    private ObjectInputStream inputStream;
    final private ObjectOutputStream outputStream;
    List<MessageModel> chatHistory;


    public ClientModel(Socket clientSocket, ObjectInputStream inputStream, ObjectOutputStream outputStream, List<MessageModel> chatHistory) {
        this.clientSocket = clientSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.chatHistory = chatHistory;
    }

    @Override
    public void run() {
        //MessageModel messageReceived;
        String msg;
        /*
        while (true) {
            try {
                // receive the string
                //messageReceived = (MessageModel) inputStream.readObject();

                //chatHistory.add(messageReceived);

                //System.out.println(messageReceived.getSender() + ": " + messageReceived.getContent());

//                if(messageReceived.equals("BYE")){  //replace with the method to exit
//                    this.clientSocket.close();
//                    break;
//                }
                msg = (String) inputStream.readObject();

                System.out.println(clientSocket.getInetAddress() +": " + msg);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

         */

//        try {
//            inputStream.close();
//            outputStream.close();
//
//        }catch(IOException e){
//            e.printStackTrace();
//        }
    }
}
