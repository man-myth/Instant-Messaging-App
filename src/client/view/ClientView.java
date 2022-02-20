package client.view;

import server.model.ChatRoomModel;
import server.model.MessageModel;
import server.model.UserModel;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.WindowAdapter;

public class ClientView extends JFrame {

    JPanel mainPanel, contactsPanel;
    // Changes: Jpanel -> MembersPanel
    MembersPanel membersPanel;
    ChatPanel chatPanel;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem logOut;
    static Font headingFont = new Font("Calibri", Font.PLAIN, 20);

    public ClientView(UserModel user, ChatRoomModel publicChat) {
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        logOut = new JMenuItem("Log Out");
        menu.add(logOut);
        menuBar.add(menu);

        mainPanel = new JPanel(new BorderLayout());
        contactsPanel = new ContactsPanel(user.getContacts());
        chatPanel = new ChatPanel(publicChat);
        membersPanel = new MembersPanel(publicChat.getUsers());
        mainPanel.add(contactsPanel, BorderLayout.WEST);
        mainPanel.add(chatPanel, BorderLayout.CENTER);
        mainPanel.add(membersPanel, BorderLayout.EAST);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(mainPanel);
        this.setJMenuBar(menuBar);
        this.setTitle(user.getUsername());
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
    }

    public String getMessage() {
        return chatPanel.getMessageTextArea().getText();
    }

    public void clearTextArea() {
        chatPanel.getMessageTextArea().setText("");
    }

    public void setWindowAdapter(WindowAdapter adapter) {
        this.addWindowListener(adapter);
    }

    public void setMessageListener(ActionListener a) {
        chatPanel.getMessageTextArea().addActionListener(a);
    }

    public void setRoom(String roomName) {

    }

    public void updateContacts(List<UserModel> users) {
        mainPanel.remove(contactsPanel);
        contactsPanel = new ContactsPanel(users);
        mainPanel.add(contactsPanel, BorderLayout.WEST);
        mainPanel.revalidate();
    }

    public static ImageIcon scaleIcon(String filename) {
        ImageIcon imageIcon = new ImageIcon(filename);
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);

        return imageIcon;
    }

    public void setMemberButtonsActionListener(ActionListener listener) {
        membersPanel.setMemberButtonsActionListener(listener);
    }

    public void addMessage(MessageModel msg) {
        chatPanel.addMessage(msg);
    }

    public void setAddButtonActionListener(ActionListener listener) {
        membersPanel.setAddButtonActionListener(listener);
    }

    public void settingsButtonListener(ActionListener listener) {
        membersPanel.settingsButtonListener(listener);
    }

    class ChatPanel extends JPanel {
        JLabel roomName;
        JTextPane content;
        public JTextField messageTextArea;
        JScrollPane scrollPane;

        ChatPanel(ChatRoomModel chatRoom) {
            content = new JTextPane();
            content.setEditable(false);
            content.setFocusable(false);
            for (MessageModel message : chatRoom.getChatHistory()) {
                addText(content, message.getSender().getUsername() + ": " + message.getContent());
            }
            scrollPane = new JScrollPane(content);
            messageTextArea = new HintTextField("Message");
            messageTextArea.setPreferredSize(new Dimension(550, 35));
            messageTextArea.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));

            this.roomName = new JLabel(chatRoom.getName(), SwingConstants.CENTER);
            this.roomName.setFont(ClientView.headingFont);
            this.setLayout(new BorderLayout());
            this.add(this.roomName, BorderLayout.NORTH);
            this.add(this.scrollPane, BorderLayout.CENTER);
            this.add(this.messageTextArea, BorderLayout.SOUTH);
            this.setPreferredSize(new Dimension(550, 420));
        }

        public void addMessage(MessageModel message) {
            this.remove(scrollPane);
            addText(content, message.getSender().getUsername() + ": " + message.getContent());
            scrollPane = new JScrollPane(content);
            this.add(scrollPane, BorderLayout.CENTER);
            this.revalidate();
        }

        public void addText(JTextPane pane, String text) {
            StyledDocument doc = pane.getStyledDocument();

            Style style = pane.addStyle("Color Style", null);
            StyleConstants.setForeground(style, Color.BLACK);
            try {
                doc.insertString(doc.getLength(), text + "\n", style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        public void addColoredText(JTextPane pane, String text, Color color) {
            StyledDocument doc = pane.getStyledDocument();

            Style style = pane.addStyle("Color Style", null);
            StyleConstants.setForeground(style, color);
            try {
                doc.insertString(doc.getLength(), text, style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        public JTextField getMessageTextArea() {
            return messageTextArea;
        }
    }

    class ContactsPanel extends JPanel {
        JPanel panel;
        JTextField searchBar;
        JScrollPane scrollPane;

        public ContactsPanel(List<UserModel> users) {
            JLabel contactsLabel = new JLabel("Contacts", SwingConstants.CENTER);
            contactsLabel.setFont(ClientView.headingFont);

            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new ContactButton("Public Chat", false));
            for (UserModel user : users) {
                panel.add(new ContactButton(user.getUsername(), user.getUnreadMessages() == null));
            }
            scrollPane = new JScrollPane(panel);
            scrollPane.setPreferredSize(new Dimension(100, 430));

            searchBar = new HintTextField("Search Contacts");
            searchBar.setPreferredSize(new Dimension(200, 35));

            this.setLayout(new BorderLayout());
            this.add(contactsLabel, BorderLayout.NORTH);
            this.add(scrollPane, BorderLayout.CENTER);
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
        List<MemberButton> memberButtons;

        public MembersPanel(List<UserModel> users) {
            searchBar = new HintTextField("Search Members");
            searchBar.setPreferredSize(new Dimension(200, 25));

            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            memberButtons = new ArrayList<>();
            for (UserModel user : users) {
                MemberButton button = new MemberButton(user.getUsername());
                memberButtons.add(button);
                panel.add(button);
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

        public void setMemberButtonsActionListener(ActionListener listener) {
            for (MemberButton button : memberButtons) {
                button.addActionListener(listener);
            }
        }

        public void setAddButtonActionListener(ActionListener listener) {
            addButton.addActionListener(listener);
        }

        public void setButtonActionListener(ActionListener listener) {
            addButton.addActionListener(listener);
        }

        public void settingsButtonListener(ActionListener listener) {
            settingsButton.addActionListener(listener);
        }
    }
}
