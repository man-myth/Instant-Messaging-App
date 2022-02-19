package server.model;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandlerModel implements Runnable {
    private final Socket clientSocket;

    public ClientHandlerModel(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());) {
            AuthenticatorModel authenticate;
            UserModel currentUser = new UserModel();
            try {
                Object input;
                while (true) {
                    input = inputStream.readObject();
                    System.out.println(input);
                    if (input.equals("login")) {
                        authenticate = new AuthenticatorModel(inputStream, outputStream,
                                ServerModel.getRegisteredUsers());
                        String username = (String) inputStream.readObject();
                        String password = (String) inputStream.readObject();
                        System.out.printf("Attempting to login with username:%s and password:%s\n", username, password);

                        if (authenticate.verifyUser(username, password)) {
                            System.out.println("Success!");
                            outputStream.writeObject("VERIFIED");
                            outputStream.writeObject(getUserFromList(username, password));
                            currentUser = getUserFromList(username, password);
                            outputStream.writeObject(ServerModel.getPublicChat());
                        } else {
                            System.out.println("Failed.");
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
                        publicChat.getChatHistory().add((MessageModel) inputStream.readObject());
                        Utility.exportPublicChat(publicChat);

                        outputStream.writeObject(publicChat);

                        //Testing
                    } else if (input.equals("get contacts")) {
                        List<UserModel> contacts = currentUser.getContacts();
                        // testing only
                        contacts.add(new UserModel("testing", "123"));
                        contacts.add(new UserModel("testing1", "123"));
                        contacts.add(new UserModel("testing2", "123"));
                        for (UserModel u : contacts) {
                            outputStream.writeObject(u.getUsername());
                        }
                        outputStream.writeObject("done");

                        //Adding contact to room
                    } else if (input.equals("add contact to room")) {
                        outputStream.writeObject("done");
                        // to do add user to chat room
                    }
                }
            } catch (ClassNotFoundException e) {
                System.out.println(clientSocket + "has disconnected.");
                clientSocket.close();
                // e.printStackTrace();
            } catch (EOFException e1){
                System.out.println(clientSocket + "has disconnected.");
                clientSocket.close();
                currentUser.setActive(false);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public UserModel getUserFromList(String username, String password) {
        return ServerModel.registeredUsers.stream()
                .filter(user -> username.equals(user.getUsername()) && password.equals(user.getPassword())).findAny()
                .orElse(null);
    }
}
