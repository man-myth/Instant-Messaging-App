import client.controller.ClientController;
import client.controller.LoginController;
import client.model.ClientModel;
import server.model.ChatRoomModel;
import server.model.MessageModel;
import server.model.UserModel;
import server.controller.AdminController;
import server.model.Utility;

import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private static LoginController logInController;
    private static ClientController clientController;
    private static AdminController adminController;
    private static UserModel user;
    private static Socket socket;

    public static void main(String[] args) {

        /*
        List users = new ArrayList<>();
        users.add(new UserModel("admin", "root"));
        Utility.exportData(users);
         */
        // Start client
        LoginController logInController = new LoginController();
        logInController.run();
        /*
        List<MessageModel> messages = new ArrayList<>();
        messages.add(new MessageModel(new UserModel("test", "test"), new ChatRoomModel("test"), "Hello!", LocalTime.now(), LocalDate.now()));
        messages.add(new MessageModel(new UserModel("test", "test"), new ChatRoomModel("test"), "Hi!", LocalTime.now(), LocalDate.now()));
        messages.add(new MessageModel(new UserModel("test", "test"), new ChatRoomModel("test"), "stfu", LocalTime.now(), LocalDate.now()));
        ChatRoomModel publicChat = new ChatRoomModel("", Utility.readUsersData("res/data.dat"), messages);
        Utility.exportPublicChat(publicChat);
         */
    }
}
