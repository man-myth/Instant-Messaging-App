package server.model;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
            System.out.print("Chat rooms: ");
            for (ChatRoomModel chatRoom : user.getChatRooms()) {
                System.out.print(chatRoom.getName() + " ");
            }
            System.out.println();
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
                        outputStream.writeObject("VERIFIED");
                        currentUser = getUserFromList(username, password);
                        writeObject(currentUser);
                        writeObject(ServerModel.getPublicChat());
                    } else {
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
                    ChatRoomModel publicChat = ServerModel.getPublicChat();
                    MessageModel newMessage = (MessageModel) inputStream.readObject();
                    publicChat.getChatHistory().add(newMessage);

                    Utility.exportPublicChat(publicChat);
                    ServerModel.setPublicChat(Utility.readPublicChat("res/publicChat.dat"));

                    for (ClientHandlerModel client : ServerModel.clients) {
                        if (client.equals(this)) {
                            continue;
                        }
                        client.writeObject("broadcast");
                        client.writeObject(newMessage);
                    }
                } else if (input.equals("add contact")) {
                    String username = (String) inputStream.readObject();
                    UserModel user = getUserFromList(username);
                    // Run if user is not null and user is not yet a contact of current user
                    if (user != null && !currentUser.hasContact(username)) {
                        // Contains initial list of chat room members
                        List<UserModel> users = new ArrayList<>();
                        users.add(currentUser);
                        users.add(user);

                        // Create new chat room for current user
                        ChatRoomModel newChatRoom = new ChatRoomModel(username, users, new ArrayList<>());
                        currentUser.getContacts().add(user);
                        currentUser.getChatRooms().add(newChatRoom);
                        ServerModel.updateUser(currentUser.getUsername(), currentUser);
                        // Add chat room for other user
                        user.getContacts().add(currentUser);
                        newChatRoom = new ChatRoomModel(currentUser.getUsername(), users, new ArrayList<>());
                        user.getChatRooms().add(newChatRoom);
                        ServerModel.updateUser(user.getUsername(), user);

                        // Save data
                        Utility.exportUsersData(ServerModel.getRegisteredUsers());
                        ServerModel.setRegisteredUsers(Utility.readUsersData("res/data.dat"));
                        currentUser = getUserFromList(currentUser.getUsername());

                        outputStream.writeObject("contact added");
                        outputStream.writeObject(currentUser.getChatRooms());

                        // Update client view of new contact if new contact is logged in
                        for (ClientHandlerModel client : ServerModel.clients) {
                            if (client.currentUser == null || client.equals(this)) {
                                continue;
                            }

                            System.out.println(client.currentUser.getUsername());
                            if (client.currentUser.getUsername().equals(user.getUsername())) {
                                client.currentUser = getUserFromList(user.getUsername());
                                client.writeObject("contact added");
                                client.writeObject(client.currentUser.getChatRooms());
                                break;
                            }
                        }
                    }
                } else if (input.equals("get room")) {
                    currentUser = getUserFromList(currentUser.getUsername());
                    String roomName = (String) inputStream.readObject();
                    outputStream.writeObject("return room");
                    ChatRoomModel chatRoom = roomName.equals("Public Chat") ? ServerModel.getPublicChat() : getChatRoomFromList(currentUser, roomName);
                    outputStream.writeObject(chatRoom);
                } else if (input.equals("send message")) {
                    MessageModel newMessage = (MessageModel) inputStream.readObject();
                    String roomName = newMessage.getReceiver().getName();

                    ChatRoomModel senderChatRoom = getChatRoomFromList(currentUser, roomName);
                    senderChatRoom.getChatHistory().add(newMessage);
                    currentUser.updateChatroom(senderChatRoom.getName(), senderChatRoom);
                    ServerModel.updateUser(currentUser.getUsername(), currentUser);

                    List<UserModel> receivers = senderChatRoom.getUsers();
                    // Update chat history for receivers
                    for (UserModel user : receivers) {
                        if (!user.getUsername().equals(currentUser.getUsername())) {
                            // If chat room is a private room
                            if (senderChatRoom.getUsers().size() == 2) {
                                ChatRoomModel receiverChatRoom = getChatRoomFromList(user, currentUser.getUsername());
                                receiverChatRoom.setChatHistory(senderChatRoom.getChatHistory());
                                user.updateChatroom(currentUser.getUsername(), receiverChatRoom);
                                // Else if chat room is a group chat
                            } else {
                                user.updateChatroom(senderChatRoom.getName(), senderChatRoom);
                            }
                            ServerModel.updateUser(user.getUsername(), user);
                        }
                    }
                    Utility.exportUsersData(ServerModel.getRegisteredUsers());
                    ServerModel.setRegisteredUsers(Utility.readUsersData("res/data.dat"));
                    currentUser = getUserFromList(currentUser.getUsername());
                    for (ClientHandlerModel client : ServerModel.clients) {
                        if (client.equals(this)) {
                            continue;
                        }
                        outputStream.writeObject("new message");
                    }

                } else if (input.equals("update username")) {
                    String[] names = (String[]) inputStream.readObject();
                    for (UserModel u : ServerModel.registeredUsers) {
                        if (u.getUsername().equals(names[0]))
                            u.setUsername(names[1]);
                    }
                    Utility.exportUsersData(ServerModel.getRegisteredUsers());
                    System.out.println("username changed for " + names[1]);
                } else if (input.equals("update password")) {
                    String name = (String) inputStream.readObject();
                    String password = (String) inputStream.readObject();
                    for (UserModel u : ServerModel.registeredUsers) {
                        if (u.getUsername().equals(name))
                            u.setPassword(password);
                    }
                    Utility.exportUsersData(ServerModel.getRegisteredUsers());
                    System.out.println("password changed for " + name);
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
        } finally {
            currentUser = null;
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

    public ChatRoomModel getChatRoomFromList(UserModel currentUser, String roomName) {
        return currentUser.getChatRooms().stream().filter(room -> room.getName().equals(roomName)).findAny().orElse(null);
    }
}
