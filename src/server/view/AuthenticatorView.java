package server.view;

import client.view.HintPasswordField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AuthenticatorView {
    JFrame frame = new JFrame();
    public AuthenticatorView(){
        frame.setAlwaysOnTop(true);
    }

    public void promptEnterUsername(){
        JOptionPane.showMessageDialog(frame, "Error! Please enter a username.","Error", JOptionPane.ERROR_MESSAGE);
        frame.dispose();
    }

    public void promptAlreadyLoggedIn(String username, String password){
        JOptionPane.showMessageDialog(frame, username + " is already logged in!","Error", JOptionPane.ERROR_MESSAGE);
        frame.dispose();
    }

    public void promptDoesNotExist(String username){
        JOptionPane.showMessageDialog(frame, username + " does not exist!","Error", JOptionPane.ERROR_MESSAGE);
        frame.dispose();
    }

    public void promptWrongPassword(){
        JOptionPane.showMessageDialog(frame, "Wrong password! Try again.","Error", JOptionPane.ERROR_MESSAGE);
        frame.dispose();
    }

    public int promptChangePass(){
        return JOptionPane.showConfirmDialog(frame, "Do you want to change password?", "Change password", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
    }

    //--- NEW PASSWORD VIEW
    public static class AskNewPass extends JFrame {
        // -Fields
        JButton change = new JButton("change");
        JPasswordField pass = new HintPasswordField("New password");
        JPasswordField rePass = new HintPasswordField("Confirm new password");
        JLabel label = new JLabel("Enter new password");

        // -Constructor
        public AskNewPass() {
            pass.setPreferredSize(new Dimension(200, 25));
            rePass.setPreferredSize(new Dimension(200, 25));
            change.setFocusable(false);

            // frame details 1
            this.setLayout(new FlowLayout());
            this.add(label);
            this.add(pass);
            this.add(rePass);
            this.add(change);

            //// frame details 1
            this.setPreferredSize(new Dimension(250, 170));
            this.pack();
            this.setVisible(true);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        // -Methods

        // adds action listener to change button
        public void changeListener(ActionListener listener) {
            change.addActionListener(listener);
        }

        // gets the input text from text field
        public String getPass() {
            return new String(pass.getPassword());
        }

        // gets the input text from text field
        public String getRePass() {
            return new String(rePass.getPassword());
        }

        // if passwords did not match, prompt an error
        public void promptError(boolean isValid) {
            if (!isValid)
                JOptionPane.showMessageDialog(this.getContentPane(), "Password did not match, try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
        }

        public void changeSuccess(boolean isDone) {
            if (isDone) {
                JOptionPane.showMessageDialog(null, "Password successfully changed",
                        "Password Changed", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        }
    }// end of AskNewPass
}
