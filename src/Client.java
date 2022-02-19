import client.controller.LoginController;


public class Client {

    public static void main(String[] args) {
        //List users = new ArrayList<>();
        //users.add(new UserModel("admin", "root"));
        //Utility.exportData(users);
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
