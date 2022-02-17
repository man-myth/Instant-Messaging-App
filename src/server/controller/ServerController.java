package server.controller;

import client.controller.ClientController;
import server.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * run server controller
 * run the server
 * read the list of users
 * wait for validate users
 */
public class ServerController {
    public ServerModel serverModel;


    public ServerController() {
        serverModel = new ServerModel(Utility.readData("res/data.dat"), new ArrayList<>());
    }

    public void run() {
    }
}
