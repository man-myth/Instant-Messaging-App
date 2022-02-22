import server.model.ChatRoomModel;
import server.model.MessageModel;
import server.model.UserModel;
import server.model.Utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UpdateDatFiles {
    public static void main(String[] args) {
        List users = new ArrayList<>();
        users.add(new UserModel("admin", "root"));
        users.add(new UserModel("user1", "pass1"));
        users.add(new UserModel("user2", "pass2"));
        users.add(new UserModel("user3", "pass3"));
        users.add(new UserModel("user4", "pass4"));
        users.add(new UserModel("user5", "pass5"));
        users.add(new UserModel("user6", "pass6"));
        users.add(new UserModel("user7", "user7"));
        users.add(new UserModel("user8", "user8"));
        users.add(new UserModel("user9", "user9"));
        users.add(new UserModel("user10", "user10"));
        Utility.exportUsersData(users);
        List<MessageModel> messages = new ArrayList<>();
        messages.add(new MessageModel(new UserModel("test", "test"), new ChatRoomModel("test", ""), "Hello!", LocalTime.now(), LocalDate.now()));
        messages.add(new MessageModel(new UserModel("test", "test"), new ChatRoomModel("test", ""), "Hi!", LocalTime.now(), LocalDate.now()));
        messages.add(new MessageModel(new UserModel("test", "test"), new ChatRoomModel("test", ""), "shhh", LocalTime.now(), LocalDate.now()));
        //ChatRoomModel publicChat = new ChatRoomModel("Public Chat", Utility.readUsersData("res/data.dat"), messages);
        //Utility.exportPublicChat(publicChat);
    }
}
