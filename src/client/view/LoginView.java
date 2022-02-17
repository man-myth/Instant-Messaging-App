package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;

public class LoginView extends JFrame {

    JPanel mainPanel, buttonsPanel;
    public JTextField usernameTextField;
    public JPasswordField passwordTextField;
    public JButton loginButton, registerButton;
    Dimension dimension;

    public LoginView() {
        mainPanel = new JPanel();
        buttonsPanel = new JPanel();

        dimension = new Dimension(200, 25);

        usernameTextField = new HintTextField("Username");
        usernameTextField.setPreferredSize(dimension);
        passwordTextField = new HintPasswordField("Password");
        passwordTextField.setPreferredSize(dimension);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        buttonsPanel.add(loginButton);
        buttonsPanel.add(registerButton);
        buttonsPanel.setBackground(Color.WHITE);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(usernameTextField);
        mainPanel.add(passwordTextField);
        mainPanel.add(buttonsPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(mainPanel);
        this.setTitle("Login");
        this.pack();
        this.setLocationRelativeTo(null);
    }

    public void setWindowAdapter(WindowAdapter adapter) {
        this.addWindowListener(adapter);
    }

}
