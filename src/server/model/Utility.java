package server.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Utility {


    public static void exportUsersData(List<UserModel> users) {
        try (
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("res/data.dat"));

        ) {
            for (UserModel user : users) {
                outputStream.writeObject(user);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<UserModel> readUsersData(String filename) {
        List<UserModel> users = new ArrayList<>();
        try (
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));
        ) {
            UserModel u;
            while (true) {
                try {
                    u = (UserModel) inputStream.readObject();
                    users.add(u);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static ChatRoomModel readPublicChat(String filename) {
        ChatRoomModel publicChat = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            publicChat = (ChatRoomModel) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return publicChat;
    }

    public static void exportPublicChat(ChatRoomModel publicChat) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("res/publicChat.dat"))) {
            outputStream.writeObject(publicChat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
