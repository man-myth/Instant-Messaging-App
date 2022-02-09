import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    String username;
    String password;
    String status;
    String userType;
    List<User> contacts;
    List<User> bookmarks;
    List<ChatRoom> chatRooms;
    List<Message> unreadMessages;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = "";
        this.userType = "";
        this.contacts = new ArrayList<>();
        this.chatRooms = new ArrayList<>();
    }

    public void sortNames(List<User> nameList) {

    }

    public User searchUser(User user) {
        return null;
    }
}
