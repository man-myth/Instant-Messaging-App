package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class MessageModel {
    UserModel sender;
    ChatRoomModel receiver;
    String content;
    LocalTime time;
    LocalDate date;
}
