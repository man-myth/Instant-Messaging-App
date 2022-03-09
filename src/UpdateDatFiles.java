import common.ChatRoomModel;
import common.MessageModel;
import common.UserModel;
import server.model.Utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UpdateDatFiles {
    public static void main(String[] args) {
        List<UserModel> users = new ArrayList<>();
        users.add(new UserModel("admin", "root"));
        users.add(new UserModel("gwyn", "gwyn"));
        users.add(new UserModel("bill", "bill"));
        users.add(new UserModel("jethro", "jethro"));
        users.add(new UserModel("matt", "matt"));
        users.add(new UserModel("mayn", "mayn"));
        users.add(new UserModel("steph", "steph"));
        users.add(new UserModel("meet", "meet"));
//        users.add(new UserModel("user8", "user8"));
//        users.add(new UserModel("user9", "user9"));
//        users.add(new UserModel("user10", "user10"));

        //add all users to admin
        Utility.exportUsersData(users);

        List<MessageModel> messages = new ArrayList<>();
//        messages.add(new MessageModel(new UserModel("admin", "test"), new ChatRoomModel("Public Chat", ""), "Hello!", LocalTime.now(), LocalDate.now()));
//        messages.add(new MessageModel(new UserModel("user1", "test"), new ChatRoomModel("Public Chat", ""), "Hi!", LocalTime.now(), LocalDate.now()));
//        messages.add(new MessageModel(new UserModel("user2", "test"), new ChatRoomModel("Public Chat", ""), "shhh", LocalTime.now(), LocalDate.now()));
        ChatRoomModel publicChat = new ChatRoomModel("Public Chat", Utility.readUsersData("res/data.dat"), messages, "");
        publicChat.setUsers(users);
        Utility.exportPublicChat(publicChat);

        for (UserModel u : users) {
            u.addChatRoom(publicChat);
        }

        for(UserModel u: users){
            if(!u.getUsername().equals("admin")){
                //create array list for the new room
                ArrayList<UserModel> userRooms = new ArrayList<>();
                userRooms.add(u);
                userRooms.add(users.get(0));

                //add contact and room to admin
                users.get(0).getContacts().add(u);
                users.get(0).addChatRoom(new ChatRoomModel(u.getUsername(), userRooms, new ArrayList<>(),""));

                //add contact and room to user
                u.getContacts().add(users.get(0));
                u.addChatRoom(new ChatRoomModel(users.get(0).getUsername(), userRooms, new ArrayList<>(),""));
            }
        }

        Utility.exportUsersData(users);
    }
}