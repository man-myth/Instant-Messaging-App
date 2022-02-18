package server.controller;

import server.view.AdminView;
import client.view.ExitOnCloseAdapter;
import server.model.ChatRoomModel;
import server.model.MessageModel;
import server.model.UserModel;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import client.view.LoginView;
import server.model.UserModel;


public class AdminController {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private UserModel admin;
    ChatRoomModel currentRoom;
    AdminView adminView;


    public AdminController(Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream,UserModel user, ChatRoomModel adminRoom ){
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.admin = admin;
        this.currentRoom = adminRoom;
    }

    /*
    To follow:
     run method, and additional methods for admin privileges such as delete

     */




}
