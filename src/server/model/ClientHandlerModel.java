package server.model;

import java.io.*;
import java.net.Socket;

/**
 * Handles client connected to the server
 */
public class ClientHandlerModel implements Runnable {
    private final Socket clientSocket;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;
    UserModel currentUser;

    // constructor
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

        // Handle requests of the client
        currentUser = new UserModel();
        try {
            while (!clientSocket.isClosed()) {
                Object input;
                input = inputStream.readObject();

                if (input.equals("login")) {
                    AuthenticatorModel authenticate = new AuthenticatorModel(inputStream, outputStream,
                            ServerModel.getRegisteredUsers());
                    String username = (String) inputStream.readObject();
                    String password = (String) inputStream.readObject();
                    System.out.printf("Attempting to login with username:%s and password:%s\n", username, password);
                    if (authenticate.verifyUser(username, password)) {
                        System.out.println("Success!");
                        outputStream.writeObject("VERIFIED");
                        currentUser = getUserFromList(username, password);
                        writeObject(currentUser);
                        writeObject(ServerModel.getPublicChat());
                    } else {
                        System.out.println("Failed.");
                        outputStream.writeObject("FAILED");
                    }
                } else if (input.equals("register")) {
                    System.out.println("Attempting to register.");
                    String username = (String) inputStream.readObject();
                    String password = (String) inputStream.readObject();
                    UserModel newUser = new UserModel(username, password);

                    // if username already exists, prompt a message
                    if (ServerModel.doesUsernameExist(newUser.getUsername()))
                        outputStream.writeObject("invalid");

                    else {
                        ServerModel.addRegisteredUser(newUser);
                        ServerModel.getPublicChat().addUser(newUser);
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
                } else if (input.equals("add contact to room")) {
                    UserModel user = (UserModel) inputStream.readObject();
                    // todo add user to chat room

                } else if (input.equals("kick contact from room")) {

                } else if (input.equals("add contact")) {
                    String username = (String) inputStream.readObject();
                    UserModel user = getUserFromList(username);
                    // Run if user is not null and user is not yet a contact of current user
                    if (user != null && !currentUser.hasContact(username)) {
                        currentUser.getContacts().add(user);
                        user.getContacts().add(currentUser);
                        for (ClientHandlerModel client : ServerModel.clients) {
                            if (client.equals(this)) {
                                continue;
                            }
                            if (client.currentUser.getUsername().equals(user.getUsername())) {
                                client.writeObject("contact added");
                                client.writeObject(currentUser);
                                break;
                            }
                        }
                        outputStream.writeObject("contact added");
                        outputStream.writeObject(user);
                        Utility.exportUsersData(ServerModel.getRegisteredUsers());
                    }
                }

                // Changes: Removed input.equals("get contacts") since ClientModel.user already
                // has a contact list

            }
        } catch (ClassNotFoundException | IOException e) {
            System.out.println(clientSocket + "has disconnected.");
            currentUser.setActive(false);
            // e.printStackTrace();
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

    public UserModel getUserFromList(String username) {
        return ServerModel.registeredUsers.stream()
                .filter(user -> username.equals(user.getUsername())).findAny()
                .orElse(null);
    }

    public UserModel getUserFromList(String username, String password) {
        return ServerModel.registeredUsers.stream()
                .filter(user -> username.equals(user.getUsername()) && password.equals(user.getPassword())).findAny()
                .orElse(null);
    }
}
