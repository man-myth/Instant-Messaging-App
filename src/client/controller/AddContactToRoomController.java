package client.controller;
import client.model.AddContactToRoomModel;
import client.view.AddContactToRoomView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


class AddContactToRoomController {

    public AddContactToRoomController(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        AddContactToRoomModel model = new AddContactToRoomModel();
        String[] contacts = model.returnContacts(inputStream,outputStream);
        AddContactToRoomView view = new AddContactToRoomView(contacts);

        //adds the contact to list
        view.addActionListener((e) ->{
            String username = view.getSelected();
            model.addUser(inputStream,outputStream,username);
        });
    }
}
