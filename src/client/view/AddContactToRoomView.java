package client.view;

import server.model.UserModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class AddContactToRoomView extends JFrame {
    JButton addButton = new JButton("Add");

    AddContactToRoomView(String contacts){
        //Combo box
        String[] test = {"hi", "hello"};
        JComboBox comboBox = new JComboBox(test);

        //frame details 1
        this.setPreferredSize(new Dimension(300,300));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());

        //add to frame
        this.add(comboBox);
        this.add(addButton);

        //frame details 2
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public void addActionListener(ActionListener listener){
        addButton.addActionListener(listener);
    }

}
