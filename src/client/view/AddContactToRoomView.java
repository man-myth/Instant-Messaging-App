package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

//Adding contact to room
public class AddContactToRoomView extends JFrame {
    JButton addButton = new JButton("Add");
    JLabel label = new JLabel("Add a contact to this room");
    JComboBox comboBox;

    public AddContactToRoomView(String[] strings) {
        // Combo box
        comboBox = new JComboBox(strings);

        // buttons
        addButton.setFocusable(false);

        // frame details 1
        this.setPreferredSize(new Dimension(300, 100));
        this.setLayout(new FlowLayout());
        this.setTitle("Add to room");

        // add to frame
        this.add(label);
        this.add(comboBox);
        this.add(addButton);

        // frame details 2
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public void setAddButtonActionListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void successMessage() {
        JOptionPane.showMessageDialog(null, "Successfully added user " + getSelected(), "Added",
                JOptionPane.INFORMATION_MESSAGE);
        this.dispose();
    }

    public void errorUserIsHere() {
        JOptionPane.showMessageDialog(this.getContentPane(), "User " + getSelected() + " is already in this room.", "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void errorInvalidAction() {
        JOptionPane.showMessageDialog(this.getContentPane(), "No user found.", "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public String getSelected() {
        return Objects.requireNonNull(comboBox.getSelectedItem()).toString();
    }

}
