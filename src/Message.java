import java.time.LocalDate;
import java.time.LocalTime;

public class Message {
    User sender;
    ChatRoom receiver;
    String content;
    LocalTime time;
    LocalDate date;
}
