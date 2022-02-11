package controller;

import model.UserModel;

import java.net.Socket;

public class MainController {
    private static LogInController logInController;
    private static ClientController clientController;
    private static AdminController adminController;
    private static UserModel user;
    private static Socket socket;

    public static void main(String[] args) {

        logInController = new LogInController();  //show the login frame
        user = logInController.getUser();         // get the user that logged in

        if(user.getAdmin()){    // if the user is admin, render the admin client
            adminController = new AdminController(user); // show the client for admin
        } else {
            clientController = new ClientController(socket); // else show the client controller
        }

    }


}
