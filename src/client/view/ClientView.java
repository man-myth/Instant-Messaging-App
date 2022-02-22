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
import java.awt.event.TextListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.WindowAdapter;

public class ClientView extends JFrame {

    JPanel mainPanel, contactsPanel;
    // Changes: Jpanel -> MembersPanel
    MembersPanel membersPanel;
    MembersPanelSearch membersPanelSearch;
    ChatPanel chatPanel;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem logOut;
    public JPanel membersCardPanel;
    public CardLayout cl = new CardLayout();
    static Font headingFont = new Font("Calibri", Font.PLAIN, 20);

    //todo textfield focus
    public ClientView(UserModel user, ChatRoomModel publicChat) {
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        logOut = new JMenuItem("Log Out");
        menu.add(logOut);
        menuBar.add(menu);

        membersCardPanel = new JPanel();
        membersCardPanel.setLayout(cl);

        mainPanel = new JPanel(new BorderLayout());
        contactsPanel = new ContactsPanel(user.getContacts());
        chatPanel = new ChatPanel(publicChat);
        membersPanel = new MembersPanel(user, publicChat);
        membersPanelSearch = new MembersPanelSearch(user,publicChat);
        membersCardPanel.add(membersPanel, "1");
        membersCardPanel.add(membersPanelSearch, "2");
        mainPanel.add(contactsPanel, BorderLayout.WEST);
        mainPanel.add(chatPanel, BorderLayout.CENTER);
        mainPanel.add(membersCardPanel, BorderLayout.EAST);
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

    public void setAddItemActionListener(ActionListener listener) {
        for (MembersPanel.MemberButton button : membersPanel.getMemberButtons()) {
            button.getPopupMenu().setAddItemActionListener(listener);
        }
        membersPanelSearch.memberButton.getPopupMenu().setAddItemActionListener(listener);
    }

    public void setBookmarkListener(ActionListener listener){
        for (MembersPanel.MemberButton button : membersPanel.getMemberButtons()) {
            button.getPopupMenu().setBookmarkActionListener(listener);
        }
        membersPanelSearch.memberButton.getPopupMenu().setBookmarkActionListener(listener);
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
        membersPanel.addButton.addActionListener(listener);
    }

    public void setKickButtonActionListener(ActionListener listener){
        membersPanel.kickButton.addActionListener(listener);
    }

    public void settingsButtonListener(ActionListener listener) {
        membersPanel.settingsButton.addActionListener(listener);
    }

    public void addNewMember(UserModel user){
        membersPanel.addNewMember(user);
    }

    public void kickMember(UserModel user){
        membersPanel.kickMember(user);
    }

    public void changeUsername(String oldName, String newName){
        membersPanel.changeUsername(oldName,newName);
    }

    public void searchBarActionListener(ActionListener listener){
        membersPanel.searchBar.addActionListener(listener);
    }

    public void setSearchTextListener2(TextListener listener){
        membersPanelSearch.searchBar2.addTextListener(listener);
    }

    public void showMemberPane1(){
        membersPanel.searchBar.setText("");
        cl.show(membersCardPanel, "1");
    }

    public void showMemberPane2(){
        membersPanelSearch.searchBar2.setText(membersPanel.searchBar.getText());
        cl.show(membersCardPanel, "2");
    }


    public void runChangeButtonName(String username){
        membersPanelSearch.changeButtonName(username);
    }




    /*---------- INNER CLASSES ----------*/

    /*ChatPanel Class*/
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

    /*ContactsPanel Class*/
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

    /*MembersPanel Class*/
    class MembersPanel extends JPanel{
        JPanel panel, settingsPanel;
        JButton addButton, kickButton, settingsButton;
        JScrollPane scrollPane;
        public List<MemberButton> memberButtons;
        JTextField searchBar;

        public MembersPanel(UserModel user, ChatRoomModel publicChat) {
            List<UserModel> users =  publicChat.getUsers();
            searchBar = new HintTextField("Search Members");
            searchBar.setPreferredSize(new Dimension(200, 25));

            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            memberButtons = new ArrayList<>();
            for (UserModel u : users) {
                MemberButton button = new MemberButton(u.getUsername());
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
            if(!user.getUsername().equals(publicChat.getAdmin())){
                kickButton.setVisible(false);
            }
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
            MemberPopupMenu popupMenu;

            public MemberButton(String memberName) {
                this.setMinimumSize(new Dimension(175, 35));
                this.setPreferredSize(new Dimension(200, 35));
                this.setMaximumSize(new Dimension(200, 35));
                this.setText(memberName);

                popupMenu = new MemberPopupMenu();
                this.setComponentPopupMenu(popupMenu);

                imageIcon = new ImageIcon("res/graphics/user.png");

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

        public void setMemberButtonsActionListener(ActionListener listener) {
            for (MemberButton button : memberButtons) {
                button.addActionListener(listener);
            }
        }

        public List<MemberButton> getMemberButtons() {
            return memberButtons;
        }

        //add a new member button
        public void addNewMember(UserModel user){
            MemberButton button = new MemberButton(user.getUsername());
            memberButtons.add(button);
            panel.add(button);
            this.revalidate();
        }

        //remove a member button
        public void kickMember(UserModel user){
            String name = user.getUsername();
            //loops to find the button with the same name
            for(JButton b: memberButtons){
                if(b.getText().equals(name)) {
                    panel.remove(b);
                    break;
                }
            }
            memberButtons.removeIf(e -> e.getText().equals(name));
            this.repaint();
            this.revalidate();
        }

        //change username button
        public void changeUsername(String oldName, String newName){
            for(JButton b: memberButtons){
                if(b.getText().equals(oldName)) {
                    b.setText(newName);
                    break;
                }
            }
        }

    }//end of MembersPanel class

    /*MembersPanelSearch Class*/
    class MembersPanelSearch extends JPanel{
        JPanel panel, settingsPanel;
        JButton addButton, kickButton, settingsButton;
        MemberButton memberButton;
        JScrollPane scrollPane;
        List<UserModel> users;
        TextField searchBar2;

        public MembersPanelSearch(UserModel user, ChatRoomModel publicChat) {
            users = publicChat.getUsers();
            searchBar2 = new TextField();
            searchBar2.setPreferredSize(new Dimension(200, 25));

            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            memberButton = new MemberButton("");
            panel.add(memberButton);

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
            if(!user.getUsername().equals(publicChat.getAdmin())){
                kickButton.setVisible(false);
            }
            this.setLayout(new BorderLayout());
            this.setBackground(Color.GREEN);
            this.add(searchBar2, BorderLayout.NORTH);
            this.add(scrollPane, BorderLayout.CENTER);
            this.add(settingsPanel, BorderLayout.SOUTH);
            this.setMinimumSize(new Dimension(200, 500));
            this.setPreferredSize(new Dimension(200, 500));
            this.setMaximumSize(new Dimension(200, 500));
        }

        class MemberButton extends JButton {
            ImageIcon imageIcon;
            MemberPopupMenu popupMenu;

            public MemberButton(String memberName) {
                this.setMinimumSize(new Dimension(175, 35));
                this.setPreferredSize(new Dimension(200, 35));
                this.setMaximumSize(new Dimension(200, 35));
                this.setText(memberName);

                popupMenu = new MemberPopupMenu();
                this.setComponentPopupMenu(popupMenu);

                imageIcon = new ImageIcon("res/graphics/user.png");

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
        }//member button

        public void changeButtonName(String username){
            for(UserModel u: users){
                if(u.getUsername().equals(username)) {
                    memberButton.setText(username);
                    break;
                }else{
                    memberButton.setText("");
                }
            }
            this.repaint();
            this.revalidate();

        }
    }//end of MembersPanelSearch class

    /*MemberPopupMenu Class*/
    class MemberPopupMenu extends JPopupMenu {
        JMenuItem add;
        JMenuItem bookmark;

        public MemberPopupMenu() {
            add = new JMenuItem("Add contact");
            bookmark = new JMenuItem("Bookmark");
            this.add(add);
            this.add(bookmark);
        }

        public void setAddItemActionListener(ActionListener listener) {
            add.addActionListener(listener);
        }

        public void setBookmarkActionListener(ActionListener listener){
            bookmark.addActionListener(listener);
        }
    }
}
