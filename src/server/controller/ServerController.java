package server.controller;

import server.model.ServerModel;
import server.model.Utility;


/**
 * run server controller
 * run the server
 * read the list of users
 * wait for validate users
 */
public class ServerController {
    public ServerModel serverModel;
    public AdminController adminController;


    public ServerController() {
        serverModel = new ServerModel(Utility.readUsersData("res/data.dat"), Utility.readPublicChat("res/publicChat.dat"));
    }

    public void run() {
        serverModel.run();
    }
}
