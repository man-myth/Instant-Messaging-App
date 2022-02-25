import client.controller.LoginController;


public class Client {

    public static void main(String[] args) {
       // Start client;
        LoginController logInController = new LoginController();
        logInController.run();
    }
}
