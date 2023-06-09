package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SettingsView extends JFrame {
    // -Fields
    JButton changeName = new JButton("Change username");
    JButton changePass = new JButton("Change password");
    JButton changeStatus = new JButton("Change status");
    JButton help = new JButton("Need help?");

    // -Constructor
    public SettingsView() {
        // button details
        changeName.setFocusable(false);
        changeName.setPreferredSize(new Dimension(150, 50));

        changePass.setFocusable(false);
        changePass.setPreferredSize(new Dimension(150, 50));

        changeStatus.setFocusable(false);
        changeStatus.setPreferredSize(new Dimension(150, 50));

        help.setFocusable(false);
        help.setPreferredSize(new Dimension(150, 50));



        // frame details 1
        this.setPreferredSize(new Dimension(300, 325));
        this.setLayout(new FlowLayout());

        // add to frame
        this.add(changeName);
        this.add(changePass);
        this.add(changeStatus);
        this.add(help);

        // frame details 2
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // -Methods

    // adds action listener to changeName button
    public void changeNameActionListener(ActionListener listener) {
        changeName.addActionListener(listener);
    }

    // adds action listener to changePass button
    public void changePassActionListener(ActionListener listener) {
        changePass.addActionListener(listener);
    }

    // adds action listener to changeStatus button
    public void changeStatusActionListener(ActionListener listener) {
        changeStatus.addActionListener(listener);
    }

    // adds action listener to help button
    public void helpActionListener(ActionListener listener) {
        help.addActionListener(listener);
    }



    /*----- Inner Static Classes -----*/

    // --- NEW USERNAME VIEW
    public static class AskNewName extends JFrame {
        // -Fields
        JButton change = new JButton("change");
        JTextField textField = new HintJTextField("New username");
        JLabel label = new JLabel("Enter new username");

        // -Constructor
        public AskNewName() {
            textField.setPreferredSize(new Dimension(200, 25));
            change.setFocusable(false);

            // frame details 1
            this.setLayout(new FlowLayout());
            this.add(label);
            this.add(textField);
            this.add(change);

            // frame details 2
            this.setPreferredSize(new Dimension(250, 130));
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
        public String getText() {
            return textField.getText();
        }

        public void changeSuccess(String oldName, String newName) {
            JOptionPane.showMessageDialog(null, "Changed " + oldName + " to " + newName,
                    "Username Changed", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        }

        public void promptError() {
            JOptionPane.showMessageDialog(this.getContentPane(), "Please enter a username.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        public void promptErrorAdmin() {
            JOptionPane.showMessageDialog(this.getContentPane(), "Please enter a username.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }// end of AskNewName

    // --- NEW PASSWORD VIEW
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

    // ---STATUS VIEW
    public static class StatusView extends JFrame {
        public JButton online = new JButton("Online");
        public JButton offline = new JButton("Offline");
        public JButton afk = new JButton("AFK");
        public JButton busy = new JButton("Busy");
        public JButton disturb = new JButton("Do not disturb");
        public JButton idle = new JButton("Idle");
        public JButton invi = new JButton("Invisible");
        public JLabel status = new JLabel();

        public StatusView() {
            // buttons
            online.setFocusable(false);
            online.setPreferredSize(new Dimension(150, 50));

            offline.setFocusable(false);
            offline.setPreferredSize(new Dimension(150, 50));

            afk.setFocusable(false);
            afk.setPreferredSize(new Dimension(150, 50));

            busy.setFocusable(false);
            busy.setPreferredSize(new Dimension(150, 50));

            disturb.setFocusable(false);
            disturb.setPreferredSize(new Dimension(150, 50));

            idle.setFocusable(false);
            idle.setPreferredSize(new Dimension(150, 50));

            invi.setFocusable(false);
            invi.setPreferredSize(new Dimension(150, 50));

            // frame details 1
            this.setPreferredSize(new Dimension(280, 460));
            this.setLayout(new FlowLayout());
            this.setTitle("Set Status");
            this.setResizable(false);

            // add components
            this.add(status);
            this.add(online);
            this.add(offline);
            this.add(afk);
            this.add(busy);
            this.add(disturb);
            this.add(idle);
            this.add(invi);

            // frame details 2
            this.pack();
            this.setVisible(true);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }

        public void setCurrentStatus(String s) {
            status.setText("Current status:   " + s);
        }

        public void setLabelOnline() {
            status.setText("Current status:   Online");
        }

        public void setLabelOffline() {
            status.setText("Current status:   Offline");
        }

        public void setLabelAFK() {
            status.setText("Current status:   Away from keyboard");
        }

        public void setLabelBusy() {
            status.setText("Current status:   Busy");
        }

        public void setLabelDisturb() {
            status.setText("Current status:   Do not disturb");
        }

        public void setLabelIdle() {
            status.setText("Current status:   Idle");
        }

        public void setLabelInvi() {
            status.setText("Current status:   Invisible");
        }
    }

    // --- HELP MODULE VIEW
    public static class HelpModule extends JFrame {
        // -Fields
        String guide = "<html><h1>Frequently Asked Questions:</h1>"
                + "<br><font color = red >NOTE: When making group chats, do not make the group chat name same as the user, it won't work!</font>"
                + "<br>"
                + "<br><i>Q1: How to kick a user from public chat?</i>"
                + "<br>Unless you created the groupchat, only admin can kick a contact from public chat."
                + "<br><i>Q2: How to add a user to bookmarks?</i><"
                + "<br>Right click on the contact you want to bookmark and press 'Add to Contact'."
                + "<br><i>Q3: How to logout?</i>"
                + "<br>Click on the logout option located at Menu where you will see the option to logout.</html>";
        JLabel helpLabel = new JLabel(guide);

        // -Constructor
        public HelpModule() {

            // frame details
            this.add(helpLabel);
            helpLabel.setPreferredSize(new Dimension(250, 270));
           /* helpLabel.setLineWrap(true);
            helpLabel.setWrapStyleWord(true);
            helpLabel.setOpaque(false);
            helpLabel.setEditable(false);

            */

            // frame details 2
            this.setPreferredSize(new Dimension(300, 350));
            this.pack();
            this.setVisible(true);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
    }



}// end of SettingsView
