package client.model;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientModel {
    public static void run() {
        Scanner keyboard = new Scanner(System.in);
        int port = 2022;
        try (
                Socket socket = new Socket("localhost", port);
//                BufferedReader streamRdr = new BufferedReader(
//                        new InputStreamReader(socket.getInputStream()));
//                PrintWriter streamWtr = new PrintWriter(
//                        socket.getOutputStream(), true);

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ) {
            while (true){
                String msg = keyboard.nextLine();
                try {
                    // write on the output stream
                    outputStream.writeObject(msg);
                    System.out.println("message sent " + msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            Thread sendMessage = new Thread(() -> {
//                String msg;
//                while (true) {
//                    msg = keyboard.nextLine();
//                    System.out.println("message sent" + msg);
//                    try {
//                        // write on the output stream
//                        outputStream.writeChars(msg);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            // readMessage thread
//            Thread readMessage = new Thread(() -> {
//                while (true) {
//                    try {
//                        // read the message sent to this client
//                        String msg = (String) inputStream.readObject();
//                        System.out.println(msg);
//                    } catch (IOException | ClassNotFoundException e) {
//
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            sendMessage.start();
//            readMessage.start();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
