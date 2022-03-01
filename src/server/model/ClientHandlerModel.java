package server.model;

import common.ChatRoomModel;
import common.MessageModel;
import common.UserModel;
import server.controller.AuthenticatorController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        // Handle requests of the client
        currentUser = new UserModel();
        try {
            while (true) {
                String input = (String) inputStream.readObject();
                System.out.println(input);

                if (input.equals("login")) {
                    AuthenticatorController authenticate = new AuthenticatorController(inputStream, outputStream,
                            ServerModel.getRegisteredUsers());
                    String username = (String) inputStream.readObject();
                    String password = (String) inputStream.readObject();
                    System.out.printf("[SERVER]: Attempting to login with username:%s and password:%s\n", username, password);
                    if (authenticate.isVerified(username, password)) {
                        outputStream.writeObject("VERIFIED");
                        currentUser = getUserFromList(username, password);
                        currentUser.setActive(true);
                        currentUser.setStatus("Online");
                        ServerModel.updateUser(currentUser.getUsername(), currentUser);
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
                    newUser.addChatRoom(ServerModel.getPublicChat());

                    // if username already exists, prompt a message
                    if (ServerModel.doesUsernameExist(newUser.getUsername()))
                        outputStream.writeObject("invalid");
                    else {
                        //list for creating rooms for both new user and admin
                        ArrayList<UserModel> userRooms = new ArrayList<>();
                        userRooms.add(ServerModel.getRegisteredUsers().get(0));
                        userRooms.add(newUser);

                        //add the new user to the contact and room of admin
                        ServerModel.getRegisteredUsers().get(0).getContacts().add(newUser);
                        ServerModel.getRegisteredUsers().get(0).addChatRoom(new ChatRoomModel(newUser.getUsername(), userRooms, new ArrayList<>(), ""));

                        //add admin to contact and room of new user
                        newUser.getContacts().add(ServerModel.getRegisteredUsers().get(0));
                        newUser.addChatRoom(new ChatRoomModel(ServerModel.getRegisteredUsers().get(0).getUsername(), userRooms, new ArrayList<>(), ""));

                        ServerModel.addRegisteredUser(newUser);
                        ServerModel.getPublicChat().addUser(newUser);
                        Utility.exportUsersData(ServerModel.getRegisteredUsers());
                        outputStream.writeObject("registered");

                        for (UserModel user : ServerModel.getPublicChat().getUsers()) {
                            if (user.getUsername().equals(currentUser.getUsername()) || user.getUsername().equals(newUser.getUsername())) {
                                continue;
                            }
                            user = getUserFromList(user.getUsername());
                            user.updateChatroom("Public Chat", ServerModel.getPublicChat());
                            ServerModel.updateUser(user.getUsername(), user);
                        }

                        // Save .dat file
                        Utility.exportUsersData(ServerModel.getRegisteredUsers());
                        ServerModel.setRegisteredUsers(Utility.readUsersData("res/data.dat"));

                        // Update view for users logged in
                        for (ClientHandlerModel client : ServerModel.clients) {
                            if (client.currentUser == null || client.equals(this)) {
                                continue;
                            }
                            for (UserModel user : ServerModel.getPublicChat().getUsers()) {
                                if (user.getUsername().equals(client.currentUser.getUsername())) {
                                    client.currentUser = getUserFromList(user.getUsername());
                                    client.writeObject("update chat rooms");
                                    client.writeObject(client.currentUser.getChatRooms());
                                    break;
                                }
                            }
                        }
                    }
                } else if (input.equals("broadcast")) {
                    ChatRoomModel publicChat = ServerModel.getPublicChat();
                    MessageModel newMessage = (MessageModel) inputStream.readObject();
                    ServerModel.getPublicChat().getChatHistory().add(newMessage); //changes: used ServerModel.getPublicChat() instead of variable publicChat

                    Utility.exportPublicChat(ServerModel.getPublicChat());
                    ServerModel.setPublicChat(Utility.readPublicChat("res/publicChat.dat"));
                    currentUser.getChatRooms().set(0, ServerModel.getPublicChat()); //changes: update Public Chat of current user

                    for (ClientHandlerModel client : ServerModel.clients) {
                        if (client.equals(this)) {
                            continue;
                        }
                        client.writeObject("broadcast");
                        client.writeObject(newMessage);
                        client.currentUser.getChatRooms().set(0, ServerModel.getPublicChat()); //changes: update Public Chat of every currentUser in client
                    }
                } else if (input.equals("add contact")) {
                    String username = (String) inputStream.readObject();
                    UserModel user = getUserFromList(username);
                    boolean addingSelf = username.equals(currentUser.getUsername());
                    if (addingSelf) {
                        outputStream.writeObject("adding self");
                    }

                    if (currentUser.hasContact(username)) {
                        outputStream.writeObject("already has contact");
                    }

                    // Run if user is not null and user is not yet a contact of current user
                    if (user != null && !currentUser.hasContact(username) && !addingSelf) {
                        // Contains initial list of chat room members
                        List<UserModel> users = new ArrayList<>();
                        users.add(currentUser);
                        users.add(user);

                        // Create new chat room for current user
                        ChatRoomModel newChatRoom = new ChatRoomModel(username, users, new ArrayList<>(), "");
                        currentUser.getContacts().add(user);
                        currentUser.getChatRooms().add(newChatRoom);
                        ServerModel.updateUser(currentUser.getUsername(), currentUser);

                        // Add chat room for other user
                        user.getContacts().add(currentUser);
                        newChatRoom = new ChatRoomModel(currentUser.getUsername(), users, new ArrayList<>(), "");
                        user.getChatRooms().add(newChatRoom);
                        ServerModel.updateUser(user.getUsername(), user);

                        // Save data
                        Utility.exportUsersData(ServerModel.getRegisteredUsers());
                        ServerModel.setRegisteredUsers(Utility.readUsersData("res/data.dat"));
                        currentUser = getUserFromList(currentUser.getUsername());

                        outputStream.writeObject("contact updated");
                        outputStream.writeObject(currentUser);

                        // Update client view of new contact if new contact is logged in
                        for (ClientHandlerModel client : ServerModel.clients) {
                            if (client.currentUser == null || client.equals(this)) {
                                continue;
                            }
                            if (client.currentUser.getUsername().equals(user.getUsername())) {
                                client.currentUser = getUserFromList(user.getUsername());
                                client.writeObject("contact updated");
                                client.writeObject(client.currentUser);
                                //client.writeObject(user);
                                //changes: commented this ^ duplicate line of sending user to stream
                                break;
                            }
                        }
                    }
                } else if (input.equals("remove contact")) {
                    String username = (String) inputStream.readObject();

                    for (ChatRoomModel chatRoom : currentUser.getChatRooms()) {
                        if (chatRoom.getName().equals(username)) {
                            currentUser.getChatRooms().remove(chatRoom);
                            break;
                        }
                    }
                    for (UserModel contact : currentUser.getContacts()) {
                        if (contact.getUsername().equals(username)) {
                            currentUser.getContacts().remove(contact);
                            break;
                        }
                    }
                    ServerModel.updateUser(currentUser.getUsername(), currentUser);
                    // Save data
                    Utility.exportUsersData(ServerModel.getRegisteredUsers());
                    ServerModel.setRegisteredUsers(Utility.readUsersData("res/data.dat"));
                    currentUser = getUserFromList(currentUser.getUsername());
                    outputStream.writeObject("contact updated");
                    outputStream.writeObject(currentUser);
                } else if (input.equals("add bookmark")) {
                    String username = (String) inputStream.readObject();
                    System.out.println("adding " + username + " to bookmark");

                    ChatRoomModel room = null;
                    // find the room to bookmark from list of chatrooms
                    for (ChatRoomModel chat : currentUser.getChatRooms()) {
                        if (chat.getName().equals(username)) {
                            room = chat;
                        }
                    }
                    if (username != null && !currentUser.getBookmarks().contains(room)) {
                        // add user to bookmarks list
                        currentUser.getBookmarks().add(room);
                        ServerModel.updateUser(currentUser.getUsername(), currentUser);

                        // Save data
                        String currentStatus = currentUser.getStatus();
                        Utility.exportUsersData(ServerModel.getRegisteredUsers());
                        ServerModel.setRegisteredUsers(Utility.readUsersData("res/data.dat"));
                        currentUser = getUserFromList(currentUser.getUsername());
                        currentUser.setStatus(currentStatus);

                        outputStream.writeObject("bookmark updated");
                        outputStream.writeObject(currentUser);
                    }
                } else if (input.equals("remove bookmark")) {
                    String username = (String) inputStream.readObject();
                    System.out.println("removing " + username + " to bookmark");

                    // find the room to bookmark from list of chatrooms
                    for (ChatRoomModel chat : currentUser.getBookmarks()) {
                        if (chat.getName().equals(username)) {
                            System.out.println("before removing bookmark: " + currentUser.getBookmarks());
                            currentUser.getBookmarks().remove(chat);
                            System.out.println("after removing bookmark: " + currentUser.getBookmarks());
                            break;
                        }
                    }
                    ServerModel.updateUser(currentUser.getUsername(), currentUser);

                    // Save data
                    String currentStatus = currentUser.getStatus();
                    Utility.exportUsersData(ServerModel.getRegisteredUsers());
                    ServerModel.setRegisteredUsers(Utility.readUsersData("res/data.dat"));
                    currentUser = getUserFromList(currentUser.getUsername());
                    currentUser.setStatus(currentStatus);
                    outputStream.writeObject("bookmark updated");
                    outputStream.writeObject(currentUser);
                } else if (input.equals("suspend user")) {
                    String username = (String) inputStream.readObject();
                    System.out.println("username received: " + username);

                    List<UserModel> users = ServerModel.getRegisteredUsers();
                    System.out.println(users.get(1).getStatus() + users.get(2).getStatus() + users.get(3).getStatus());
                    UserModel toSuspend = new UserModel();

                    for (int i = 0; i < username.length(); i++) {
                        if (users.get(i).getUsername().equals(username)) {
                            toSuspend = users.get(i);
                            users.get(i).setStatus("Suspended");
                            System.out.println("suspending " + users.get(i).getUsername());
                            break;
                        }
                    }
                    System.out.println(users.get(1).getStatus() + users.get(2).getStatus() + users.get(3).getStatus());

                    Utility.exportUsersData(users);

                    ServerModel.setRegisteredUsers(Utility.readUsersData("res/data.dat"));
                    currentUser = getUserFromList(currentUser.getUsername());

                    updateUserToAll(toSuspend, "Suspended");

                } else if (input.equals("reactivate user")) {
                    String username = (String) inputStream.readObject();
                    List<UserModel> users = ServerModel.getRegisteredUsers();
                    System.out.println(users.get(1).getStatus() + users.get(2).getStatus() + users.get(3).getStatus());
                    UserModel toReactivate = new UserModel();

                    for (int i = 0; i < username.length(); i++) {
                        if (users.get(i).getUsername().equals(username)) {
                            toReactivate = users.get(i);
                            users.get(i).setStatus("Offline");
                            System.out.println("reactivating " + users.get(i).getUsername());
                            break;
                        }
                    }
                    System.out.println(users.get(1).getStatus() + users.get(2).getStatus() + users.get(3).getStatus());

                    Utility.exportUsersData(users);
                    ServerModel.setRegisteredUsers(Utility.readUsersData("res/data.dat"));
                    currentUser = getUserFromList(currentUser.getUsername());

                    updateUserToAll(toReactivate, "Offline");
                } else if (input.equals("get room")) {
                    currentUser = getUserFromList(currentUser.getUsername());
                    String roomName = (String) inputStream.readObject();

                    outputStream.writeObject("return room");
                    // ChatRoomModel chatRoom = roomName.equals("Public Chat") ? ServerModel.getPublicChat() : getChatRoomFromList(currentUser, roomName);
                    ChatRoomModel chatRoom = getChatRoomFromList(currentUser, roomName);
                    outputStream.writeObject(chatRoom);
                    outputStream.writeObject(currentUser);
                    outputStream.writeObject(currentUser.getStatus());

                    if (currentUser.roomHasUnreadMessage(roomName)) {
                        currentUser.clearUnreadMessagesFromRoom(roomName);

                        // Save data
                        ServerModel.updateUser(currentUser.getUsername(), currentUser);
                        Utility.exportUsersData(ServerModel.getRegisteredUsers());
                        currentUser = getUserFromList(currentUser.getUsername());


                    }
                } else if (input.equals("send message")) {
                    currentUser = getUserFromList(currentUser.getUsername());
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
                            if (senderChatRoom.getAdmin().equals("")) {
                                ChatRoomModel receiverChatRoom = getChatRoomFromList(user, currentUser.getUsername());
                                receiverChatRoom.setChatHistory(senderChatRoom.getChatHistory());
                                user.updateChatroom(currentUser.getUsername(), receiverChatRoom);
                                // Else if chat room is a group chat
                            } else {
                                user.updateChatroom(senderChatRoom.getName(), senderChatRoom);
                            }

                            if (!user.isActive()) {
                                System.out.println("Added message: " + newMessage.getContent() + " to " + user.getUsername() + ", receiver: " + newMessage.getReceiver().getName());

                                user.addUnreadMessage(newMessage);
                            }
                            ServerModel.updateUser(user.getUsername(), user);
                        }
                    }
                    Utility.exportUsersData(ServerModel.getRegisteredUsers());
                    ServerModel.setRegisteredUsers(Utility.readUsersData("res/data.dat"));
                    currentUser = getUserFromList(currentUser.getUsername());

                    // Update GUI of receiver if logged in
                    for (ClientHandlerModel client : ServerModel.clients) {
                        if (client.currentUser == null || client.equals(this)) {
                            continue;
                        }
                        for (UserModel user : receivers) {
                            if (client.currentUser.getUsername().equals(user.getUsername())) {
                                client.writeObject("new message");
                                if (senderChatRoom.getUsers().size() == 2) {
                                    newMessage.getReceiver().setName(currentUser.getUsername());
                                }
                                client.writeObject(newMessage);
                                break;
                            }
                        }
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
                } else if (input.equals("add contact to room")) {
                    UserModel newMember = (UserModel) inputStream.readObject();
                    ChatRoomModel room = getChatRoomFromList(currentUser, (String) inputStream.readObject());
                    // If added member to private chat room
                    if (room.getAdmin().equals("")) {
                        // Create new group chat
                        boolean repeat = true;
                        String roomName = "";
                        outputStream.writeObject("get room name");
                        while (repeat) {
                            repeat = false;
                            roomName = (String) inputStream.readObject();
                            for (UserModel user : ServerModel.getRegisteredUsers()) {
                                if (getChatRoomFromList(user, roomName) != null) {
                                    repeat = true;
                                }
                            }
                            if (repeat) {
                                System.out.println("invalid");
                                outputStream.writeObject("invalid room name");
                            }
                        }
                        System.out.println(repeat);
                        System.out.println("Hello!");
                        outputStream.writeObject("added contact to room");

                        ChatRoomModel newRoom = new ChatRoomModel(roomName, new ArrayList<>(room.getUsers()), new ArrayList<>(), currentUser.getUsername());
                        newRoom.addUser(newMember);
                        currentUser.addChatRoom(newRoom);

                        // Update chat rooms of other members
                        for (UserModel user : newRoom.getUsers()) {
                            if (user.getUsername().equals(currentUser.getUsername())) {
                                continue;
                            }
                            System.out.println("Adding " + user.getUsername() + " to " + newRoom.getName());
                            user.addChatRoom(newRoom);
                            ServerModel.updateUser(user.getUsername(), user);
                        }
                    } else { // If room is already a group chat
                        room.addUser(newMember);
                        currentUser.updateChatroom(room.getName(), room);
                        for (UserModel user : room.getUsers()) {
                            if (user.getUsername().equals(currentUser.getUsername()) || user.getUsername().equals(newMember.getUsername())) {
                                continue;
                            }
                            System.out.println("Adding " + user.getUsername() + " to " + room.getName());
                            user.updateChatroom(room.getName(), room);
                            ServerModel.updateUser(user.getUsername(), user);
                        }
                        newMember.addChatRoom(room);
                        ServerModel.updateUser(newMember.getUsername(), newMember);
                    }

                    // Save .dat file
                    ServerModel.updateUser(currentUser.getUsername(), currentUser);
                    Utility.exportUsersData(ServerModel.getRegisteredUsers());
                    ServerModel.setRegisteredUsers(Utility.readUsersData("res/data.dat"));
                    currentUser = getUserFromList(currentUser.getUsername());

                    outputStream.writeObject("update chat rooms");
                    outputStream.writeObject(currentUser.getChatRooms());

                    for (ClientHandlerModel client : ServerModel.clients) {
                        if (client.currentUser == null || client.equals(this)) {
                            continue;
                        }
                        for (UserModel user : room.getUsers()) {
                            if (user.getUsername().equals(client.currentUser.getUsername())) {
                                client.currentUser = getUserFromList(user.getUsername());
                                client.writeObject("update chat rooms");
                                client.writeObject(client.currentUser.getChatRooms());
                                break;
                            }
                        }
                    }

                } else if (input.equals("kick contact from room")) {
                    UserModel kick = getUserFromList((String) inputStream.readObject());
                    String roomName = (String) inputStream.readObject();
                    if (roomName.equals("Public Chat")) {
                        ChatRoomModel publicChat = ServerModel.getPublicChat();
                        publicChat.kickUser(kick);
                        Utility.exportPublicChat(publicChat);
                        ServerModel.setPublicChat(Utility.readPublicChat("res/publicChat.dat"));
                    }
                    ChatRoomModel room = getChatRoomFromList(currentUser, roomName);
                    room.kickUser(kick);

                    kick.removeChatRoom(room);
                    ServerModel.updateUser(kick.getUsername(), kick);

                    currentUser.updateChatroom(room.getName(), room);
                    // Kick user from other members as well
                    for (UserModel user : room.getUsers()) {
                        if (user.getUsername().equals(currentUser.getUsername()) || user.getUsername().equals(kick.getUsername())) {
                            continue;
                        }
                        user.updateChatroom(room.getName(), room);
                        ServerModel.updateUser(user.getUsername(), user);
                    }

                    // Save .dat file
                    ServerModel.updateUser(currentUser.getUsername(), currentUser);
                    Utility.exportUsersData(ServerModel.getRegisteredUsers());
                    ServerModel.setRegisteredUsers(Utility.readUsersData("res/data.dat"));
                    currentUser = getUserFromList(currentUser.getUsername());

                    outputStream.writeObject("update chat rooms");
                    outputStream.writeObject(currentUser.getChatRooms());

                    for (ClientHandlerModel client : ServerModel.clients) {
                        if (client.currentUser == null || client.equals(this)) {
                            continue;
                        }
                        if (kick.getUsername().equals(client.currentUser.getUsername())) {
                            client.currentUser = getUserFromList(kick.getUsername());
                            client.writeObject("update chat rooms");
                            client.writeObject(client.currentUser.getChatRooms());
                            break;
                        }
                        for (UserModel user : room.getUsers()) {
                            if (user.getUsername().equals(client.currentUser.getUsername())) {
                                client.currentUser = getUserFromList(user.getUsername());
                                client.writeObject("update chat rooms");
                                client.writeObject(client.currentUser.getChatRooms());
                                break;
                            }
                        }
                    }
                } else if (input.equals("change status")) {
                    String status = (String) inputStream.readObject();
                    updateStatusToAll(status);

                } else if (input.equals("read all status")) {
//                    for (ClientHandlerModel client : ServerModel.clients) {
//                        if (client.currentUser == null || client.equals(this)) {
//                            continue;
//                        }
//                        if (client.clientSocket.isClosed())
//                            continue;
//                        outputStream.writeObject("update status view");
//                        outputStream.writeObject(client.getCurrentUser().getStatus());
//                        outputStream.writeObject(client.getCurrentUser().getUsername());
//                    }
                    for(UserModel u: ServerModel.getRegisteredUsers()){
                        outputStream.writeObject("update status view");
                        outputStream.writeObject(u.getStatus());
                        outputStream.writeObject(u.getUsername());
                    }
                } else if (input.equals("logout")) {
                    updateStatusToAll("Offline");
                    currentUser.setStatus("Offline");
                    currentUser.setActive(false);
                    ServerModel.updateUser(currentUser.getUsername(), currentUser);
                    currentUser = null;
                }


                // Changes: Removed input.equals("get contacts") since ClientModel.user already
                // has a contact list

            }
        } catch (ClassNotFoundException | IOException e) {
            System.out.println(clientSocket + "has disconnected.");
            currentUser.setActive(false);
            // e.printStackTrace();
            try {
                updateStatusToAll("Offline");
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

    public void updateStatusToAll(String status) throws IOException {
        currentUser.setStatus(status);
        outputStream.writeObject("update status view");
        outputStream.writeObject(status);
        outputStream.writeObject(currentUser.getUsername());

        for (ClientHandlerModel client : ServerModel.clients) {
            //if socket is closed in the client, continue
            //if client is equal to this client, continue
            if (client.currentUser == null || client.equals(this) || client.clientSocket.isClosed()) {
                continue;
            }
            client.writeObject("update status view");
            client.writeObject(status);
            client.writeObject(currentUser.getUsername());
        }
    }

    public void updateUserToAll(UserModel user, String status) throws IOException {

        for (ClientHandlerModel client : ServerModel.clients) {
            client.writeObject("update status view");
            client.writeObject(status);
            client.writeObject(user.getUsername());
        }
    }

    public UserModel getCurrentUser() {
        return currentUser;
    }
}
