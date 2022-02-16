package server.controller;

import server.model.UserModel;
import server.model.ServerModel;

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

    public ServerController() {
        ServerModel server = new ServerModel();
        server.run();
    }
}
