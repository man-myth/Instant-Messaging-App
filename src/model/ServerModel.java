package model;

import java.util.List;

public class ServerModel {
    int port;
    List<UserModel> registeredUsers;
    List<MessageModel> chatHistory;

    private String checkStatus(String username) {
        return "";
    }

    private boolean checkUserType(String username) {
        return false;
    }

    private MessageModel searchConvo(String key) {
        return null;
    }

    private void createChat(String name) {

    }
}
