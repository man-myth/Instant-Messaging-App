package client.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import client.model.AddToContactRoomModel;
import client.view.AddContactToRoomView;

public class AddContactToRoomController {

    public AddContactToRoomController(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        AddToContactRoomModel model = new AddToContactRoomModel();
        String[] contacts = model.returnContacts(inputStream, outputStream);

        AddContactToRoomView view = new AddContactToRoomView(contacts);

        view.addActionListener((e) -> {
            String username = view.getSelected();
            model.addUser(inputStream, outputStream, username);
        });
    }

}
