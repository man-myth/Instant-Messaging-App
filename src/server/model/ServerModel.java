package server.model;

import client.controller.ClientController;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerModel {
    List<UserModel> registeredUsers;
    List<MessageModel> chatHistory;

    public ServerModel(List<UserModel> registeredUsers, List<MessageModel> chatHistory) {
        this.registeredUsers = registeredUsers;
        this.chatHistory = chatHistory;
    }

    private String checkStatus(String username) {
        return "";
    }

    private boolean checkUserType(String username) {
        return false;
    }

    private MessageModel searchConvo(String key) {
        return null;
    }

    public void createChat(String name) {

    }

    public List<UserModel> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(List<UserModel> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public List<MessageModel> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<MessageModel> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public void addRegisteredUser(UserModel user) {
        this.registeredUsers.add(user);
    }

}
