package client.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import client.model.AddContactToRoomModel;
import client.view.AddContactToRoomView;
//Adding contact to room
public class AddContactToRoomController {

    public AddContactToRoomController(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        AddContactToRoomModel model = new AddContactToRoomModel();
        String[] contacts = model.returnContacts(inputStream, outputStream);

        AddContactToRoomView view = new AddContactToRoomView(contacts);

        view.addActionListener((e) -> {
            String username = view.getSelected();
            model.addUser(inputStream, outputStream, username);
        });
    }

}
