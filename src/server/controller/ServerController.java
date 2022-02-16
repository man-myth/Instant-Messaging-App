package server.controller;

import common.UserModel;
import server.model.ServerModel;

import java.net.ServerSocket;
import java.util.ArrayList;


/**
 * run server controller
 * run the server
 * read the list of users
 * wait for validate users
 *
 */
public class ServerController {
    public static ArrayList<UserModel> users;

    public static void main(String[] args) {
        ServerModel server = new ServerModel();
        server.run();
    }
}
