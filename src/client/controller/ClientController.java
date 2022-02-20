package client.controller;

import client.model.ClientModel;
import client.view.AddContactToRoomView;
import client.view.ClientView;
import client.view.ExitOnCloseAdapter;
import server.model.ChatRoomModel;
import server.model.MessageModel;
import server.model.UserModel;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

//this class will now implement Runnable
public class ClientController implements Runnable{
    //-Fields
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    ChatRoomModel currentRoom;
    ClientView clientView;
    ClientModel clientModel;
    AddContactToRoomView addToRoomView;

    // Changes: Moved code from AddContactToRoomController to run method

    //-Constructor
    public ClientController(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
            UserModel user, ChatRoomModel publicChat) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.currentRoom = publicChat;
        this.clientModel = new ClientModel(socket, inputStream, outputStream, user);
    }

    //-Methods

    //run method when calling client controller
    @Override
    public void run() {
        System.out.println("Logged in with user: " + clientModel.getUser());
        clientView = new ClientView(clientModel.getUser(), currentRoom);
        clientView.setWindowAdapter(new ExitOnCloseAdapter(socket));

        //-adding of contact to a room
        clientView.setAddButtonActionListener(e -> {
            /*
            once the add button to room is clicked,
            get the contacts of the user and put it in the combo box view
            */
            String[] contactArray = clientModel.contactsToStringArr(clientModel.getUser().getContacts());
            addToRoomView = new AddContactToRoomView(contactArray);

            //calls the addContactToRoom method from client model if add button is clicked
            addToRoomView.setAddButtonActionListener(e1 -> {
                String username = addToRoomView.getSelected();
                clientModel.addContactToRoom(username);
            });
        });

        //-broadcasting messages
        clientView.setMessageListener(e -> {
            String message = clientView.getMessage();
            MessageModel msg = new MessageModel(clientModel.getUser(), currentRoom, message, LocalTime.now(), LocalDate.now());
            boolean doBroadcast = clientModel.broadcastMessage(message, msg);
            if(doBroadcast){
                clientView.addMessage(msg);
                clientView.clearTextArea();
            }
        });
        EventQueue.invokeLater(() -> clientView.setVisible(true));

        //-loop for broadcasting messages
        new Thread(() ->{
            while (true){
                try {
                    String event = clientModel.doEvent();
                    receiveMessage(event);

                }catch (Exception e){
                    e.printStackTrace();
                    } else if (msg.equals("done adding contact")) {

                    } else if (msg.equals("contact added")) {
                        UserModel newUser = (UserModel) inputStream.readObject();
                        clientModel.getUser().getContacts().add(newUser);

                        System.out.println(newUser.getUsername());
                        clientView.updateContacts(clientModel.getUser().getContacts());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    //e.printStackTrace();
                }
            }
        });
    }//end of run method

    //adds the new message to the client view
    public void receiveMessage(String event) throws Exception {
        if(event.equals("broadcast")){
            MessageModel message = clientModel.getMessageFromStream();
            clientView.addMessage(message);
        }
    }
}
