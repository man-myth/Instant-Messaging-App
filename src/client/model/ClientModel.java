package client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientModel {
    public static void run() {
        Scanner keyboard = new Scanner(System.in);
        int port = 2022;
        try (
                // you may replace "localhost" with ip address of server
                Socket socket = new Socket("localhost", port);
                BufferedReader streamRdr = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter streamWtr = new PrintWriter(
                        socket.getOutputStream(), true);
        ) {
            //TODO what the client will do.

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
