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
//        List users = new ArrayList<>();
//        users.add(new UserModel("admin", "root"));
//        users.add(new UserModel("user1", "pass1"));
//        users.add(new UserModel("user2", "pass2"));
//        users.add(new UserModel("user3", "pass3"));
//        users.add(new UserModel("user4", "pass4"));
//        users.add(new UserModel("user5", "pass5"));
//        users.add(new UserModel("user6", "pass6"));
//        Utility.exportUsersData(users);
//        List<MessageModel> messages = new ArrayList<>();
//        messages.add(new MessageModel(new UserModel("test", "test"), new ChatRoomModel("test", "admin"), "Hello!", LocalTime.now(), LocalDate.now()));
//        messages.add(new MessageModel(new UserModel("test", "test"), new ChatRoomModel("test", "admin"), "Hi!", LocalTime.now(), LocalDate.now()));
//        messages.add(new MessageModel(new UserModel("test", "test"), new ChatRoomModel("test", "admin"), "shhh", LocalTime.now(), LocalDate.now()));
//        ChatRoomModel publicChat = new ChatRoomModel("", Utility.readUsersData("res/data.dat"), messages);
//        Utility.exportPublicChat(publicChat);
//

       // Start client;
        LoginController logInController = new LoginController();
        logInController.run();
    }
}
