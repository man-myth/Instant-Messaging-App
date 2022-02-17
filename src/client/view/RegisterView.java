package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegisterView extends JFrame{

    JPanel mainPanel, buttonsPanel;
    JTextField usernameTextField;
    JPasswordField passwordTextField, confirmPasswordTextField;
    public JButton registerButton;
    Dimension dimension;

    public RegisterView() {
        mainPanel = new JPanel();
        buttonsPanel = new JPanel();

        dimension = new Dimension(200, 25);

        usernameTextField = new HintTextField("Username");
        usernameTextField.setPreferredSize(dimension);
        passwordTextField = new HintPasswordField("Password");
        passwordTextField.setPreferredSize(dimension);
        confirmPasswordTextField = new HintPasswordField("Confirm Password");
        confirmPasswordTextField.setPreferredSize(dimension);

        registerButton = new JButton("Register");

        buttonsPanel.add(registerButton);
        buttonsPanel.setBackground(Color.WHITE);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(usernameTextField);
        mainPanel.add(passwordTextField);
        mainPanel.add(confirmPasswordTextField);
        mainPanel.add(buttonsPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(mainPanel);
        this.setTitle("Register");
        this.pack();
        this.setLocationRelativeTo(null);
    }

    public JButton getRegisterButton() {
        return registerButton;
    }

    public String getUsername() {
        return usernameTextField.getText();
    }

    public String getPassword() {
        return new String(passwordTextField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordTextField.getPassword());
    }

    public void addRegisterListener(ActionListener listener){
        registerButton.addActionListener(listener);
    }

    //displays an error and returns true if user inputs invalid information
    public boolean promptError(boolean user, boolean pass){
        if(user) {
            JOptionPane.showMessageDialog(this.getContentPane(), "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }

        else if(pass) {
            JOptionPane.showMessageDialog(this.getContentPane(), "Password did not match, try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    //displays a message if user successfully registered
    public void successRegister(String username, boolean isError, boolean isUserValid){
        if(isError || !isUserValid) return; //if there is an error, do not proceed
        JOptionPane.showMessageDialog(null, "Registered user " + username, "Registered", JOptionPane.INFORMATION_MESSAGE);
        this.dispose();
    }

    //prompts an error if username already exist
    public void isUserValid(boolean valid, boolean isError){
        if(isError) return; //if there is an error, do not proceed

        if(!valid)
            JOptionPane.showMessageDialog(this.getContentPane(), "Username already exists. Try a different one.", "Error", JOptionPane.ERROR_MESSAGE);
    }

}
