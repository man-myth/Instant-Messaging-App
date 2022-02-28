package client.view;

import common.ChatRoomModel;
import common.MessageModel;
import common.UserModel;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.List;

public class ClientView extends JFrame {

    static Font headingFont = new Font("Calibri", Font.PLAIN, 20);
    public ContactsPanel contactsPanel;
    JPanel mainPanel;
    // Changes: Jpanel -> MembersPanel
    MembersPanel membersPanel;
    ChatPanel chatPanel;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem logOut;
    UserModel user;

    public ClientView(UserModel u, ChatRoomModel publicChat) {
        this.user = u;
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        logOut = new JMenuItem("Log Out");
        menu.add(logOut);
        menuBar.add(menu);

        mainPanel = new JPanel(new BorderLayout());
        contactsPanel = new ContactsPanel(user);
        chatPanel = new ChatPanel(publicChat);
        membersPanel = new MembersPanel(user, publicChat);
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

    public static ImageIcon scaleIcon(String filename) {
        ImageIcon imageIcon = new ImageIcon(filename);
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);

        return imageIcon;
    }

    public void setLogOutListener(ActionListener listener) {
        logOut.addActionListener(listener);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public String getInput(String prompt) {
        return JOptionPane.showInputDialog(this, prompt);
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

    public void setAddItemActionListener(ActionListener listener) {
        for (MembersPanel.MemberButton button : membersPanel.getMemberButtons()) {
            button.getPopupMenu().setAddItemActionListener(listener);
        }
    }
    public void setRemoveUserActionListener(ActionListener listener) {
        for (MembersPanel.MemberButton button : membersPanel.getMemberButtons()) {
            button.getPopupMenu().setRemoveUserActionListener(listener);
        }
    }

    public void setContactButtonsActionListener(ActionListener listener) {
        contactsPanel.setContactButtonsActionListener(listener);
    }

    public void setBookmarkButtonActionListener(ActionListener listener) {
        for (ContactsPanel.ContactButton button : contactsPanel.getButtons()) {
            button.getPopupMenu().setBookmarkButtonActionListener(listener);
        }
    }

    public void setRemoveBookmarkButtonActionListener(ActionListener listener) {
        for (ContactsPanel.ContactButton button : contactsPanel.getButtons()) {
            button.getPopupMenu().setRemoveBookmarkButtonActionListener(listener);
        }
    }

    public void setRemoveContactButtonActionListener(ActionListener listener) {
        for (ContactsPanel.ContactButton button : contactsPanel.getButtons()) {
            button.getPopupMenu().setRemoveContactButtonActionListener(listener);
        }
    }

    public void updateContacts(UserModel user) {
        mainPanel.remove(contactsPanel);
        contactsPanel = new ContactsPanel(user);
        mainPanel.add(contactsPanel, BorderLayout.WEST);
        mainPanel.revalidate();
    }

    public void updateRoom(ChatRoomModel room) {
        mainPanel.remove(chatPanel);
        chatPanel = new ChatPanel(room);
        membersPanel.clear();
        membersPanel.fillButtons(room.getUsers());
        membersPanel.revalidate();
        membersPanel.updateSettingsPanel(room);
        mainPanel.add(chatPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
    }

    public void setStatusImage(String username, String status) {
        MembersPanel.MemberButton memberButton = null;
        for (MembersPanel.MemberButton b : membersPanel.getMemberButtons()) {
            if (b.getText().equals(username)) {
                memberButton = b;
                break;
            }
        }

        switch (status) {
            case "Online" -> memberButton.setIcon(scaleIcon("res/graphics/active-user.png"));
            case "Offline", "Invisible" -> memberButton.setIcon(scaleIcon("res/graphics/user.png"));
            case "Away from keyboard" -> memberButton.setIcon(scaleIcon("res/graphics/afk-user.png"));
            case "Busy" -> memberButton.setIcon(scaleIcon("res/graphics/busy-user.png"));
            case "Do not disturb" -> memberButton.setIcon(scaleIcon("res/graphics/dont disturb-user.png"));
            case "Idle" -> memberButton.setIcon(scaleIcon("res/graphics/idle-user.png"));
        }

    }

    public void setMemberButtonsActionListener(ActionListener listener) {
        membersPanel.setMemberButtonsActionListener(listener);
    }

    public void addMessage(MessageModel msg) {
        chatPanel.addMessage(msg);
    }

    public void setAddButtonActionListener(ActionListener listener) {
        membersPanel.addButton.addActionListener(listener);
    }

    public void setKickButtonActionListener(ActionListener listener) {
        membersPanel.kickButton.addActionListener(listener);
    }

    public void settingsButtonListener(ActionListener listener) {
        membersPanel.settingsButton.addActionListener(listener);
    }

    public void addNewMember(UserModel user) {
        membersPanel.addNewMember(user);
    }

    public void kickMember(UserModel user) {
        membersPanel.kickMember(user);
    }

    public void changeUsername(String oldName, String newName) {
        membersPanel.changeUsername(oldName, newName);
    }

    public void membersSearchActionListener(TextListener listener) {
        membersPanel.searchBar.addTextListener(listener);
    }

    public void contactsSearchListener(TextListener listener) {
        contactsPanel.searchBar.addTextListener(listener);
    }

    public void changeMemberButtonPanel(String username, ChatRoomModel room) {
        membersPanel.clear();
        membersPanel.changeButtons(username, room);
        membersPanel.revalidate();
    }

    public void originalMemberButtonPanel(ChatRoomModel room) {
        membersPanel.clear();
        membersPanel.fillButtons(room.getUsers());
        membersPanel.revalidate();
    }

    public void changeContactButtons(String username, UserModel u) {
        contactsPanel.clear();
        contactsPanel.changeContactButtons(username, u);
        contactsPanel.revalidate();
    }

    public void originalContactButtons() {
        contactsPanel.clear();
        contactsPanel.fillContactButtonsSearch(contactsPanel.getButtons());
        contactsPanel.revalidate();
    }

    /**
     * Outputs dialog box stating user does not have proper permissions.
     */
    public void noPermsMsg() {
        JOptionPane.showMessageDialog(null,
                "You do not have permission to use this feature.");
    }

    public void updateSettingsPanel(ChatRoomModel currentRoom) {
        membersPanel.updateSettingsPanel(currentRoom);
    }




    /*---------- INNER CLASSES ----------*/

    /*ChatPanel Class*/
    class ChatPanel extends JPanel {
        public JTextField messageTextArea;
        JLabel roomName;
        JTextPane content;
        JScrollPane scrollPane;

        ChatPanel(ChatRoomModel chatRoom) {
            content = new JTextPane();
            content.setEditable(false);
            content.setFocusable(false);
            for (MessageModel message : chatRoom.getChatHistory()) {
                addText(content, message.getSender().getUsername() + ": " + message.getContent());
            }
            scrollPane = new JScrollPane(content);
            messageTextArea = new HintJTextField("Message");
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

    /*ContactsPanel Class*/
    class ContactsPanel extends JPanel {
        JPanel panel;
        List<ContactButton> buttons;
        TextField searchBar;
        JScrollPane scrollPane;
        ContactButton publicChatButton;


        public ContactsPanel(UserModel user) {
            JLabel contactsLabel = new JLabel("Contacts", SwingConstants.CENTER);
            contactsLabel.setFont(ClientView.headingFont);
            List<ChatRoomModel> rooms = user.getChatRooms();
            List<ChatRoomModel> bookmarkedRooms = user.getBookmarks();
            List<MessageModel> unreadMessages = user.getUnreadMessages();

            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            //publicChatButton = new ContactButton("Public Chat", false, true, true);
            buttons = new ArrayList<>();
            //buttons.add(publicChatButton);
            //panel.add(publicChatButton);

            for (ChatRoomModel bookmarkedRoom : bookmarkedRooms) {
                ContactButton button = null;
                for (MessageModel message : unreadMessages) {
                    if (message.getReceiver().getAdmin() != "") {
                        if (message.getSender().getUsername().equals(bookmarkedRoom.getName())) {
                            button = new ContactButton(bookmarkedRoom.getName(), true, true, false);
                        }
                    } else if (message.getReceiver().getName().equals(bookmarkedRoom.getName())) {
                        button = new ContactButton(bookmarkedRoom.getName(), true, true, true);
                    }
                }

                if (button == null) {
                    button = new ContactButton(bookmarkedRoom.getName(), false, true, !bookmarkedRoom.getAdmin().equals(""));
                }
                buttons.add(button);
                panel.add(button);
            }
            for (ChatRoomModel room : rooms) {
                if (!bookmarkedRooms.contains(room)) {
                    ContactButton button = null;
                    for (MessageModel message : unreadMessages) {
                        // If chat room is a private room
                        if (message.getReceiver().getAdmin().equals("")) {
                            if (message.getSender().getUsername().equals(room.getName())) {
                                button = new ContactButton(room.getName(), true, false, false);
                            }
                            // Else if room is a group chat
                        } else if (message.getReceiver().getName().equals(room.getName())) {
                            button = new ContactButton(room.getName(), true, false, true);
                        }
                    }

                    if (button == null) {
                        button = new ContactButton(room.getName(), false, false, !room.getAdmin().equals(""));
                    }
                    buttons.add(button);
                    panel.add(button);
                }
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

        public void setContactButtonsActionListener(ActionListener listener) {
            for (ContactButton button : buttons) {
                button.addActionListener(listener);
            }
        }

        public void fillContactButtonsSearch(List<ContactButton> contactButtons) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            //panel.add(publicChatButton);

            for (ContactButton b : contactButtons) {
                panel.add(b);
            }
            scrollPane = new JScrollPane(panel);
            scrollPane.setPreferredSize(new Dimension(100, 430));
            this.add(scrollPane, BorderLayout.CENTER);
        }

        public void changeContactButtons(String username, UserModel user) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            for (UserModel u : user.getContacts()) {
                if (u.getUsername().contains(username)) {
                    for (ContactButton c : buttons) {
                        if (c.getText().contains(username))
                            panel.add(c);
                    }
                }
            }
            scrollPane = new JScrollPane(panel);
            scrollPane.setPreferredSize(new Dimension(100, 430));
            this.add(scrollPane, BorderLayout.CENTER);
            this.repaint();
            this.revalidate();
        }

        public void clear() {
            contactsPanel.remove(scrollPane);
        }

        public List<ContactButton> getButtons() {
            return buttons;
        }

        class ContactButton extends JButton {
            ImageIcon imageIcon;
            ContactsPopupMenu popupMenu;
            Boolean isBookmarked;

            public ContactButton(String contactName, boolean hasUnread, boolean isBookmarked, boolean isGroup) {
                this.isBookmarked = isBookmarked;
                this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
                this.setText(contactName);
                if (hasUnread) {
                    imageIcon = isGroup ? new ImageIcon("res/graphics/has-unread-group.png") : new ImageIcon("res/graphics/has-unread.png");
                    this.setFont(this.getFont().deriveFont(Font.BOLD));
                } else {
                    imageIcon = isGroup ? new ImageIcon("res/graphics/group.png") : new ImageIcon("res/graphics/user.png");
                    this.setFont(this.getFont().deriveFont(Font.PLAIN));
                }

                popupMenu = new ContactsPopupMenu();
                this.setComponentPopupMenu(popupMenu);

                Image image = imageIcon.getImage();
                Image scaledImage = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(scaledImage);
                this.setIcon(imageIcon);
                this.setHorizontalAlignment(SwingConstants.LEFT);
                if (!isBookmarked) {
                    this.setBackground(Color.WHITE);
                } else {
                    this.setBackground(Color.LIGHT_GRAY);
                }

            }

            public ContactsPopupMenu getPopupMenu() {
                return popupMenu;
            }

            public void setBookmarked(Boolean bookmarked) {
                isBookmarked = bookmarked;
            }
        }
    }

    /*MembersPanel Class*/
    class MembersPanel extends JPanel {
        public List<MemberButton> memberButtons;
        JPanel panel, settingsPanel;
        JButton addButton, kickButton, settingsButton;
        JScrollPane scrollPane;
        TextField searchBar;
        UserModel user;

        public MembersPanel(UserModel user, ChatRoomModel chatRoom) {
            this.user = user;
            searchBar = new HintTextField("Search Members");
            searchBar.setPreferredSize(new Dimension(200, 25));
            fillButtons(chatRoom.getUsers());

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
            updateSettingsPanel(chatRoom);

            this.setLayout(new BorderLayout());
            this.setBackground(Color.GREEN);
            this.add(searchBar, BorderLayout.NORTH);
            this.add(scrollPane, BorderLayout.CENTER);
            this.add(settingsPanel, BorderLayout.SOUTH);
            this.setMinimumSize(new Dimension(200, 500));
            this.setPreferredSize(new Dimension(200, 500));
            this.setMaximumSize(new Dimension(200, 500));
        }

        public void updateSettingsPanel(ChatRoomModel chatRoom) {
            kickButton.setVisible(user.getUsername().equals(chatRoom.getAdmin()));
            settingsPanel.revalidate();
        }

        public void clear() {
            membersPanel.remove(scrollPane);
        }

        public void fillButtons(List<UserModel> users) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            memberButtons = new ArrayList<>();
            for (UserModel u : users) {
                MemberButton button = new MemberButton(u.getUsername(), u.getStatus());
                memberButtons.add(button);
                panel.add(button);
            }

            scrollPane = new JScrollPane(panel);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            this.add(scrollPane, BorderLayout.CENTER);
        }

        public void changeButtons(String username, ChatRoomModel chatRoom) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            memberButtons = new ArrayList<>();
            for (UserModel u : chatRoom.getUsers()) {
                if (u.getUsername().contains(username)) {
                    MemberButton button = new MemberButton(u.getUsername(), u.getStatus());
                    memberButtons.add(button);
                    panel.add(button);
                }
            }
            scrollPane = new JScrollPane(panel);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            this.add(scrollPane, BorderLayout.CENTER);
            this.repaint();
            this.revalidate();
        }

        public void setMemberButtonsActionListener(ActionListener listener) {
            for (MemberButton button : memberButtons) {
                button.addActionListener(listener);
            }
        }

        public List<MemberButton> getMemberButtons() {
            return memberButtons;
        }

        //add a new member button
        public void addNewMember(UserModel user) {
            MemberButton button = new MemberButton(user.getUsername(), user.getStatus());
            memberButtons.add(button);
            panel.add(button);
            this.revalidate();
        }

        //remove a member button
        public void kickMember(UserModel user) {
            String name = user.getUsername();
            //loops to find the button with the same name
            for (JButton b : memberButtons) {
                if (b.getText().equals(name)) {
                    panel.remove(b);
                    break;
                }
            }
            memberButtons.removeIf(e -> e.getText().equals(name));
            this.repaint();
            this.revalidate();
        }

        //change username button
        public void changeUsername(String oldName, String newName) {
            for (JButton b : memberButtons) {
                if (b.getText().equals(oldName)) {
                    b.setText(newName);
                    break;
                }
            }
        }

        class MemberButton extends JButton {
            ImageIcon imageIcon;
            MemberPopupMenu popupMenu;

            public MemberButton(String memberName, String status) {
                this.setMinimumSize(new Dimension(175, 35));
                this.setPreferredSize(new Dimension(200, 35));
                this.setMaximumSize(new Dimension(200, 35));
                this.setText(memberName);

                popupMenu = new MemberPopupMenu(memberName);
                this.setComponentPopupMenu(popupMenu);
                switch (status) {
                    case "Online" -> imageIcon = new ImageIcon("res/graphics/active-user.png");
                    case "Offline", "Invisible" -> imageIcon = new ImageIcon("res/graphics/user.png");
                    case "Away from keyboard" -> imageIcon = new ImageIcon("res/graphics/afk-user.png");
                    case "Busy" -> imageIcon = new ImageIcon("res/graphics/busy-user.png");
                    case "Do not disturb" -> imageIcon = new ImageIcon("res/graphics/dont disturb-user.png");
                    case "Idle" -> imageIcon = new ImageIcon("res/graphics/idle-user.png");
                }


                Image image = imageIcon.getImage();
                Image scaledImage = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(scaledImage);
                this.setIcon(imageIcon);
                this.setHorizontalAlignment(SwingConstants.LEFT);
                this.setBackground(Color.WHITE);
            }

            public MemberPopupMenu getPopupMenu() {
                return popupMenu;
            }
        }

    }//end of MembersPanel class

    /*MemberPopupMenu Class*/
    class MemberPopupMenu extends JPopupMenu {
        JMenuItem add;
        JMenuItem remove;

        public MemberPopupMenu(String username) {
            add = new JMenuItem("Add contact");
            remove = new JMenuItem("Suspend account");
            this.add(add);
            if(user.getUsername().equals("admin")){
                this.add(remove);
            }
        }

        public void setAddItemActionListener(ActionListener listener) {
            add.addActionListener(listener);
        }
        public void setRemoveUserActionListener(ActionListener listener) {
            remove.addActionListener(listener);
        }
    }

    class ContactsPopupMenu extends JPopupMenu {
        JMenuItem addtoBookmark;
        JMenuItem removeBookmark;
        JMenuItem removeContact;

        public ContactsPopupMenu() {
            addtoBookmark = new JMenuItem("Bookmark contact");
            removeBookmark = new JMenuItem("Remove bookmark");
            removeContact = new JMenuItem("Remove Contact");
            this.add(addtoBookmark);
            this.add(removeBookmark);
            this.add(removeContact);
        }

        public void setBookmarkButtonActionListener(ActionListener listener) {
            addtoBookmark.addActionListener(listener);
        }

        public void setRemoveBookmarkButtonActionListener(ActionListener listener) {
            removeBookmark.addActionListener(listener);
        }

        public void setRemoveContactButtonActionListener(ActionListener listener) {
            removeContact.addActionListener(listener);
        }
    }
}
