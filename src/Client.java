import client.controller.ClientController;
import client.controller.LogInController;
import client.model.ClientModel;
import server.model.UserModel;
import server.controller.AdminController;

import java.net.Socket;

public class Client {

    private static LogInController logInController;
    private static ClientController clientController;
    private static AdminController adminController;
    private static UserModel user;
    private static Socket socket;

    public static void main(String[] args) {
        // start client

//        logInController = new LogInController();  //show the login frame
//        user = logInController.getUser();         // get the user that logged in
//
//
//        clientController = new ClientController(socket, user);

        ClientModel client = new ClientModel();
        client.run();
    }
}
