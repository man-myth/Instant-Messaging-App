package controller;

import model.UserModel;
import view.RegisterView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Register pseudo
 * - implement action listener to this class
 * - Created getters for the buttons and textfield in the RegisterView class
 * - initialize JButtons for register and login using getter methods
 * - add this actionListener to the initialized buttons
 * - set visible to true so that the GUI will open
 * - user will enter username and pass
 * - if user clicks register:
 *      controller will get the contents from the username and pass textfield using the getter methods
 *      if user entered an empty username:
 *          prompt error message (console gui as of now)
 *      if password and confirm password is not the same:
 *          prompt error message (console gui as of now)
 *      else:
 *          Creates a new UserModel object
 *          Adds the new object to the registered users (not yet implemented)
 */
public class RegisterController implements ActionListener {
    private final RegisterView view = new RegisterView(); //instantiate RegisterView object
    private final JButton register = view.getRegisterButton(); //gets the register button from RegisterView
    private final JButton login = view.getLoginButton(); //gets the login button from RegisterView
    private String userName;
    private String password;
    private String reEnteredPass;


    //setters
    public void setPassword(String password) {this.password = password;}
    public void setReEnteredPass(String reEnteredPass) {this.reEnteredPass = reEnteredPass;}
    public void setUserName(String userName) {this.userName = userName;}

    //getters
    public String getPassword() {return password;}
    public String getReEnteredPass() {return reEnteredPass;}
    public String getUserName() {return userName;}


    //method that opens up the registration GUI
    public void openRegistrationGUI() {
        register.addActionListener(this);
        login.addActionListener(this);
        view.getLoginButton().addActionListener(this);
        view.setVisible(true); //opens up the registration view
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setUserName(view.getUsernameTextField().getText());
        setPassword(new String(view.getPasswordTextField().getPassword()));
        setReEnteredPass(new String(view.getConfirmPasswordTextField().getPassword()));

        //if user clicks register
        if (e.getSource() == register) {
            if(userName.equals(""))
                System.out.println("Please enter a username");
            if(!password.equals(reEnteredPass))
                System.out.println("invalid password");
            else {
                UserModel user = new UserModel(userName, password, false);
                //Todo = add user object to server
            }
        }//end of register button---

        //if user clicks login
        else if(e.getSource() == login){
            //ToDO
        }//end of login button---
    }
}
