package server.model;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandlerModel implements Runnable {
    private final Socket clientSocket;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;

    public ClientHandlerModel(Socket socket) {
        this.clientSocket = socket;
        try {
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            while (true) {
                Object input;
                input = inputStream.readObject();
                System.out.print("read: ");
                System.out.println(input);

                if (input.equals("login")) {
                    System.out.println("at");
                    AuthenticatorModel authenticate;
                    authenticate = new AuthenticatorModel(inputStream, outputStream, ServerModel.getRegisteredUsers());
                    String username = (String) inputStream.readObject();
                    String password = (String) inputStream.readObject();
                    System.out.printf("Attempting to login with username:%s and password:%s\n", username, password);
                    if (authenticate.verifyUser(username, password)) {
                        System.out.println("Success!");
                        writeObject("VERIFIED");
                        writeObject(getUserFromList(username, password));
                        writeObject(ServerModel.getPublicChat());
                    } else {
                        System.out.println("Failed.");
                    }
                } else if (input.equals("register")) {
                    System.out.println("Attempting to register.");
                    input = inputStream.readObject();

                    ServerModel.addRegisteredUser((UserModel) input);
                    Utility.exportUsersData(ServerModel.getRegisteredUsers());

                    UserModel newUser = (UserModel) input;

                    //if username already exists, prompt a message
                    if (ServerModel.doesUsernameExist(newUser.getUsername()))
                        writeObject("invalid");

                    else {
                        ServerModel.addRegisteredUser(newUser);
                        Utility.exportUsersData(ServerModel.getRegisteredUsers());
                        writeObject("registered");
                    }
                } else if (input.equals("broadcast")) {
                    ChatRoomModel publicChat = ServerModel.publicChat;
                    MessageModel newMessage = (MessageModel) inputStream.readObject();
                    publicChat.getChatHistory().add(newMessage);
                    Utility.exportPublicChat(publicChat);

                    for (ClientHandlerModel client : ServerModel.clients) {
                        if (client.equals(this)) {
                            continue;
                        }
                        client.writeObject("broadcast");
                        client.writeObject(newMessage);
                    }
                    //outputStream.writeObject(publicChat);
                }


            }
        } catch (ClassNotFoundException | IOException e) {
            System.out.println(clientSocket + "has disconnected.");
            //e.printStackTrace();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public void writeObject(Object object) {
        try {
            outputStream.writeObject(object);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean equals(ClientHandlerModel another) {
        return this.clientSocket.equals(another.clientSocket);
    }


    public UserModel getUserFromList(String username, String password) {
        return ServerModel.registeredUsers.stream().filter(user -> username.equals(user.getUsername()) && password.equals(user.password)).findAny().orElse(null);
    }
}
