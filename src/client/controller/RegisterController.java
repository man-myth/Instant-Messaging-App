
package client.controller;

import client.model.RegisterModel;
import client.view.RegisterView;
import common.UserModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// this will show the GUI for registering the user,
// and it will add the user to the data file

public class RegisterController{
    private final RegisterView view;
    private final RegisterModel model;

    //Constructor for register controller
    public RegisterController(RegisterView view, RegisterModel model){
        this.view = view;
        this.model = model;
        view.addRegisterListener(new RegisterListener());
        view.addLoginListener(new LoginListener());
    }

    //action listener for registration
    class RegisterListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String userName = view.getName();
            String password = view.getPassword();
            String reEnteredPass = view.getConfirmPassword();
            model.register(userName, password, reEnteredPass);

        }
    }//end of RegisterListener

    //todo
    class LoginListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }//end of LoginListener


}
