package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SettingsView extends JFrame {
    //-Fields
    JButton changeName = new JButton("Change username");
    JButton changePass = new JButton("Change password");
    JButton changeStatus = new JButton("Change status");

    //-Constructor
    public SettingsView(){
        //button details
        changeName.setFocusable(false);
        changeName.setPreferredSize(new Dimension(150,50));

        changePass.setFocusable(false);
        changePass.setPreferredSize(new Dimension(150,50));

        changeStatus.setFocusable(false);
        changeStatus.setPreferredSize(new Dimension(150,50));

        // frame details 1
        this.setPreferredSize(new Dimension(300, 210));
        this.setLayout(new FlowLayout());

        // add to frame
        this.add(changeName);
        this.add(changePass);
        this.add(changeStatus);

        // frame details 2
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    //-Methods

    //adds action listener to changeName button
    public void changeNameActionListener(ActionListener listener){
        changeName.addActionListener(listener);
    }

    //adds action listener to changePass button
    public void changePassActionListener(ActionListener listener){
        changePass.addActionListener(listener);
    }

    //adds action listener to changeStatus button
    public void changeStatusActionListener(ActionListener listener){
        changeStatus.addActionListener(listener);
    }

/*----- Inner Static Classes -----*/

    //---JFrame class that takes the new username
    public static class AskNewName extends JFrame{
        //-Fields
        JButton change = new JButton("change");
        JTextField textField = new HintTextField("New username");
        JLabel label = new JLabel("Enter new username");

        //-Constructor
        public AskNewName(){
            textField.setPreferredSize(new Dimension(200, 25));
            change.setFocusable(false);

            //frame details 1
            this.setLayout(new FlowLayout());
            this.add(label);
            this.add(textField);
            this.add(change);

            //frame details 2
            this.setPreferredSize(new Dimension(250,130));
            this.pack();
            this.setVisible(true);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        //-Methods

        //adds action listener to change button
        public void changeListener(ActionListener listener){
            change.addActionListener(listener);
        }

        //gets the input text from text field
        public String getText(){
            return textField.getText();
        }

        public void changeSuccess(String oldName, String newName, boolean isDone){
            if(isDone) {
                JOptionPane.showMessageDialog(null, "Changed " + oldName + " to " + newName,
                        "Username Changed", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }else
                JOptionPane.showMessageDialog(this.getContentPane(), "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//end of AskNewName

    //---JFrame class that takes the new password
    public static class AskNewPass extends JFrame{
        //-Fields
        JButton change = new JButton("change");
        JPasswordField pass = new HintPasswordField("New password");
        JPasswordField rePass = new HintPasswordField("Confirm new password");
        JLabel label = new JLabel("Enter new password");

        //-Constructor
        public AskNewPass(){
            pass.setPreferredSize(new Dimension(200, 25));
            rePass.setPreferredSize(new Dimension(200, 25));
            change.setFocusable(false);

            //frame details 1
            this.setLayout(new FlowLayout());
            this.add(label);
            this.add(pass);
            this.add(rePass);
            this.add(change);

            ////frame details 1
            this.setPreferredSize(new Dimension(250,170));
            this.pack();
            this.setVisible(true);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        //-Methods

        //adds action listener to change button
        public void changeListener(ActionListener listener){
            change.addActionListener(listener);
        }

        //gets the input text from text field
        public String getPass(){
            return new String(pass.getPassword());
        }

        //gets the input text from text field
        public String getRePass(){
            return new String(rePass.getPassword());
        }

        //if passwords did not match, prompt an error
        public void promptError(boolean isValid){
            if(!isValid)
                JOptionPane.showMessageDialog(this.getContentPane(), "Password did not match, try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//end of AskNewPass

}//end of SettingsView




