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
        for (UserModel user : ServerModel.getRegisteredUsers()) {
            System.out.println(user);
        }

        UserModel currentUser = new UserModel();
        try {
            while (true) {
                Object input;
                input = inputStream.readObject();
                if (input.equals("login")) {
                    AuthenticatorModel authenticate = new AuthenticatorModel(inputStream, outputStream, ServerModel.getRegisteredUsers());
                    String username = (String) inputStream.readObject();
                    String password = (String) inputStream.readObject();
                    System.out.printf("Attempting to login with username:%s and password:%s\n", username, password);
                    if (authenticate.verifyUser(username, password)) {
                        System.out.println("Success!");
                        outputStream.writeObject("VERIFIED");
                        writeObject(getUserFromList(username, password));
                        currentUser = getUserFromList(username, password);
                        writeObject(ServerModel.getPublicChat());
                    } else {
                        System.out.println("Failed.");
                        outputStream.writeObject("FAILED");
                    }
                } else if (input.equals("register")) {
                    System.out.println("Attempting to register.");
                    input = inputStream.readObject();
                    UserModel newUser = (UserModel) input;

                    // if username already exists, prompt a message
                    if (ServerModel.doesUsernameExist(newUser.getUsername()))
                        outputStream.writeObject("invalid");

                    else {
                        ServerModel.addRegisteredUser(newUser);
                        Utility.exportUsersData(ServerModel.getRegisteredUsers());
                        outputStream.writeObject("registered");
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
                    // Testing
                } else if (input.equals("add contact to room")) {
                    UserModel user = (UserModel) inputStream.readObject();
                    outputStream.writeObject("done adding contact");
                    // to do add user to chat room
                } else if (input.equals("add contact")) {
                    String username = (String) inputStream.readObject();
                }

                // Changes: Removed input.equals("get contacts") since ClientModel.user already has a contact list

            }
        } catch (ClassNotFoundException | IOException e) {
            System.out.println(clientSocket + "has disconnected.");
            currentUser.setActive(false);
            //e.printStackTrace();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                // e.printStackTrace();
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
        return ServerModel.registeredUsers.stream()
                .filter(user -> username.equals(user.getUsername()) && password.equals(user.getPassword())).findAny()
                .orElse(null);
    }
}
