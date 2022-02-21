package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

//Adding contact to room
public class KickContactFromRoomView extends JFrame {
    JButton kickButton = new JButton("Kick");
    JLabel label = new JLabel("Kick a member from this room");
    JComboBox comboBox;

    public KickContactFromRoomView(String[] strings) {
        // Combo box
        comboBox = new JComboBox(strings);

        // buttons
        kickButton.setFocusable(false);

        // frame details 1
        this.setPreferredSize(new Dimension(300, 130));
        this.setLayout(new FlowLayout());
        this.setTitle("Kick from room");

        // add to frame
        this.add(label);
        this.add(comboBox);
        this.add(kickButton);

        // frame details 2
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public void setKickButtonActionListener(ActionListener listener) {
        kickButton.addActionListener(listener);
    }

    public void successMessage() {
        JOptionPane.showMessageDialog(null, "Successfully kicked user " + getSelected(), "Kicked",
                JOptionPane.INFORMATION_MESSAGE);
        this.dispose();
    }

    public void errorInvalidAction(){
        JOptionPane.showMessageDialog(this.getContentPane(), "No user found.", "Error",
                JOptionPane.ERROR_MESSAGE);
    }


    public String getSelected() {
        return comboBox.getSelectedItem().toString();
    }

}
