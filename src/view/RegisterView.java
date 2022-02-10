package view;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JFrame{

    JPanel mainPanel, buttonsPanel;
    JTextField usernameTextField;
    JPasswordField passwordTextField, confirmPasswordTextField;
    JButton loginButton, registerButton;
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

        loginButton = new JButton("Login");
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
}