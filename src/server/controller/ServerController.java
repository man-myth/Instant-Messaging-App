package server.controller;

import client.controller.RegisterController;
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
    ServerModel server;
    public static ArrayList<UserModel> users;

    public ServerController() {
        server = new ServerModel();
        server.run();
    }



}
