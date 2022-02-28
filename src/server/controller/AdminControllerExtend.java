package server.controller;
import client.controller.ClientController;
import common.ChatRoomModel;
import common.UserModel;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AdminControllerExtend extends ClientController {
    Socket socket;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;
    UserModel userModel;
    ChatRoomModel model;
    public AdminControllerExtend(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream, UserModel user, ChatRoomModel publicChat) {
        super(socket, inputStream, outputStream, user, publicChat);
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.userModel = user;
        model = publicChat;
    }
}