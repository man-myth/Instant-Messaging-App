import client.controller.ClientController;
import client.controller.LoginController;
import client.model.ClientModel;
import server.model.UserModel;
import server.controller.AdminController;

import java.net.Socket;

public class Client {

    private static LoginController logInController;
    private static ClientController clientController;
    private static AdminController adminController;
    private static UserModel user;
    private static Socket socket;

    public static void main(String[] args) {

        // List users = new ArrayList<>();
        // users.add(new UserModel("admin", "root"));
        // Utility.exportData(users);
        // Start client
        ClientController clientController = new ClientController();
        clientController.run();
    }
}
