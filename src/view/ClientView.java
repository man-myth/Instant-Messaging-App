package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


public class ClientView extends JFrame {

    JPanel mainPanel, contactsPanel, membersPanel, chatPanel;
    static Font headingFont = new Font("Calibri", Font.PLAIN, 20);

    public ClientView() {
        mainPanel = new JPanel(new BorderLayout());
        contactsPanel = new ContactsPanel();
        chatPanel = new ChatPanel("Room Name");
        membersPanel = new MembersPanel();

        mainPanel.add(contactsPanel, BorderLayout.WEST);
        mainPanel.add(chatPanel, BorderLayout.CENTER);
        mainPanel.add(membersPanel, BorderLayout.EAST);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(mainPanel);
        this.setTitle("Messenger");
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
    }

    public static ImageIcon scaleIcon(String filename) {
        ImageIcon imageIcon = new ImageIcon(filename);
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);

        return imageIcon;
    }
}

class ChatPanel extends JPanel {
    JLabel roomName;
    JTextPane content;
    JTextArea messageTextArea;
    JScrollPane scrollPane;

    ChatPanel(String roomName) {
        content = new JTextPane();
        content.setEditable(false);
        content.setFocusable(false);
        scrollPane = new JScrollPane(content);
        messageTextArea = new HintTextArea("Message");
        messageTextArea.setPreferredSize(new Dimension(550, 35));
        messageTextArea.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));

        this.roomName = new JLabel(roomName, SwingConstants.CENTER);
        this.roomName.setFont(ClientView.headingFont);

        this.setLayout(new BorderLayout());
        this.add(this.roomName, BorderLayout.NORTH);
        this.add(this.scrollPane, BorderLayout.CENTER);
        this.add(this.messageTextArea, BorderLayout.SOUTH);
        this.setPreferredSize(new Dimension(550, 420));
    }
}

class ContactsPanel extends JPanel {
    JPanel panel;
    JTextField searchBar;

    public ContactsPanel() {
        JLabel contactsLabel = new JLabel("Contacts", SwingConstants.CENTER);
        contactsLabel.setFont(ClientView.headingFont);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new ContactButton("Contact", false));
        panel.add(new ContactButton("Contact", true));
        panel.setPreferredSize(new Dimension(100, 430));

        searchBar = new HintTextField("Search Contacts");
        searchBar.setPreferredSize(new Dimension(200, 35));

        this.setLayout(new BorderLayout());
        this.add(contactsLabel, BorderLayout.NORTH);
        this.add(new JScrollPane(panel), BorderLayout.CENTER);
        this.add(searchBar, BorderLayout.SOUTH);
        this.setMinimumSize(new Dimension(200, 500));
        this.setPreferredSize(new Dimension(200, 500));
        this.setMaximumSize(new Dimension(200, 500));
    }


    class ContactButton extends JButton {
        ImageIcon imageIcon;

        public ContactButton(String contactName, boolean hasUnread) {
            this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            this.setText(contactName);
            if (hasUnread) {
                imageIcon = new ImageIcon("res/graphics/has-unread.png");
            } else {
                imageIcon = new ImageIcon("res/graphics/user.png");
            }

            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);
            this.setIcon(imageIcon);
            this.setHorizontalAlignment(SwingConstants.LEFT);
            this.setBackground(Color.WHITE);
        }
    }
}

class MembersPanel extends JPanel {
    JPanel panel, settingsPanel;
    JButton addButton, kickButton, settingsButton;
    JScrollPane scrollPane;
    JTextField searchBar;

    public MembersPanel() {
        searchBar = new HintTextField("Search Members");
        searchBar.setPreferredSize(new Dimension(200, 25));

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (int i = 0; i < 20; i++) {
            panel.add(new MemberButton("Member"));
        }
        scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


        settingsPanel = new JPanel(new GridLayout());
        addButton = new JButton(ClientView.scaleIcon("res/graphics/add-user.png"));
        addButton.setBackground(Color.WHITE);
        kickButton = new JButton(ClientView.scaleIcon("res/graphics/remove-user.png"));
        kickButton.setBackground(Color.WHITE);
        settingsButton = new JButton(ClientView.scaleIcon("res/graphics/gear.png"));
        settingsButton.setBackground(Color.WHITE);

        settingsPanel.add(addButton);
        settingsPanel.add(kickButton);
        settingsPanel.add(settingsButton);
        settingsPanel.setPreferredSize(new Dimension(200, 35));

        this.setLayout(new BorderLayout());
        this.setBackground(Color.GREEN);

        this.add(searchBar, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(settingsPanel, BorderLayout.SOUTH);
        this.setMinimumSize(new Dimension(200, 500));
        this.setPreferredSize(new Dimension(200, 500));
        this.setMaximumSize(new Dimension(200, 500));
    }

    class MemberButton extends JButton {
        ImageIcon imageIcon;

        public MemberButton(String memberName) {
            this.setMinimumSize(new Dimension(175, 35));
            this.setPreferredSize(new Dimension(200, 35));
            this.setMaximumSize(new Dimension(200, 35));
            this.setText(memberName);

            imageIcon = new ImageIcon("res/graphics/user.png");

            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);
            this.setIcon(imageIcon);
            this.setHorizontalAlignment(SwingConstants.LEFT);
            this.setBackground(Color.WHITE);
        }
    }
}