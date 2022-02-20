package server.controller;

import server.model.*;

import java.net.Socket;
import java.util.ArrayList;


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
        adminController = new AdminController();
    }

    public void run() {
        serverModel.run();
        adminController.run();
    }
}
