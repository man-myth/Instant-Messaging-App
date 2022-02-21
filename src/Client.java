import client.controller.LoginController;
import server.model.ChatRoomModel;
import server.model.MessageModel;
import server.model.UserModel;
import server.model.Utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class Client {

    public static void main(String[] args) {

       // Start client;
        LoginController logInController = new LoginController();
        logInController.run();
    }
}
