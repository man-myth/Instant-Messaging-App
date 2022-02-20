package client.view;

import server.model.UserModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
//Adding contact to room
public class AddContactToRoomView extends JFrame {
    JButton addButton = new JButton("Add");
    JComboBox comboBox;

    public AddContactToRoomView(String[] strings) {
        // Combo box
        comboBox = new JComboBox(strings);

        //buttons
        addButton.setFocusable(false);

        // frame details 1
        this.setPreferredSize(new Dimension(300, 100));
        this.setLayout(new FlowLayout());

        // add to frame
        this.add(comboBox);
        this.add(addButton);

        // frame details 2
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void setAddButtonActionListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void successMessage(){
        JOptionPane.showMessageDialog(null, "Successfully added user "+getSelected(), "Added", JOptionPane.INFORMATION_MESSAGE);
        this.dispose();
    }

    public String getSelected() {
        return comboBox.getSelectedItem().toString();
    }
}
