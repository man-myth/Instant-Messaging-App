package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


public class View extends JFrame {

    JPanel mainPanel, contactsPanel, membersPanel, chatPanel;
    static Font headingFont = new Font("Calibri", Font.PLAIN, 20);

    public View() {
        mainPanel = new JPanel(new BorderLayout());
        contactsPanel = new ContactsPanel();
        chatPanel = new ChatPanel("Room Name");
        membersPanel = new MembersPanel();

        mainPanel.add(contactsPanel, BorderLayout.WEST);
        mainPanel.add(chatPanel, BorderLayout.CENTER);
        mainPanel.add(membersPanel, BorderLayout.EAST);

        this.add(mainPanel);
        this.setTitle("Messenger");
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
    }
}

class LoginPanel extends JPanel {
    LoginPanel() {

        JTextField usernameTextField = new HintTextField("Username");
        JTextField passwordTextField = new HintTextField("Password");
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            boolean loggedIn = false;
            /*
            for (model.User user : users) {
                if (usernameTextField.getText().equals(user.username) && passwordTextField.getText().equals(user.password)) {
                    loggedIn = true;
                }
            }
             */
            if (loggedIn) {
                System.out.println("Logged in");
            } else {
                System.out.println("Account doesn't exist");
            }
        });

        this.add(usernameTextField);
        this.add(passwordTextField);
        this.add(loginButton);
    }
}

class ChatPanel extends JPanel {
    JLabel roomName;
    JTextPane content;
    JTextField messageTextField;
    JScrollPane scrollPane;

    ChatPanel(String roomName) {
        content = new JTextPane();
        content.setEditable(false);
        content.setFocusable(false);
        scrollPane = new JScrollPane(content);
        messageTextField = new HintTextField("model.Message");
        messageTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        this.roomName = new JLabel(roomName, SwingConstants.CENTER);
        this.roomName.setFont(View.headingFont);

        this.setLayout(new BorderLayout());
        this.add(this.roomName, BorderLayout.NORTH);
        this.add(this.scrollPane, BorderLayout.CENTER);
        this.add(this.messageTextField, BorderLayout.SOUTH);
        this.setPreferredSize(new Dimension(550, 420));
    }
}

class ContactsPanel extends JPanel {
    JPanel panel;
    JTextField searchBar;

    public ContactsPanel() {
        JLabel contactsLabel = new JLabel("Contacts", SwingConstants.CENTER);
        contactsLabel.setFont(View.headingFont);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new ContactButton("Contact"));
        panel.add(new ContactButton("Contact"));
        panel.add(new ContactButton("Contact"));
        panel.add(new ContactButton("Contact"));
        panel.add(new ContactButton("Contact"));
        panel.setPreferredSize(new Dimension(100, 420));

        searchBar = new HintTextField("Search");

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(contactsLabel);
        this.add(new JScrollPane(panel));
        this.add(searchBar);
    }

    class ContactButton extends JButton {
        public ContactButton(String contactName) {
            this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            this.setText(contactName);
        }
    }
}

class MembersPanel extends JPanel {
    JPanel panel, settingsPanel;
    JButton addButton, kickButton, settingsButton;
    JTextField searchBar;

    public MembersPanel() {
        searchBar = new HintTextField("Search");
        //searchBar.setMaximumSize(new Dimension(200, 50));

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new MemberButton("Member"));
        panel.add(new MemberButton("Member"));
        panel.add(new MemberButton("Member"));
        panel.add(new MemberButton("Member"));
        panel.add(new MemberButton("Member"));
        panel.add(new MemberButton("Member"));

        panel.setPreferredSize(new Dimension(200, 420));
        panel.setMaximumSize(new Dimension(200, 420));

        settingsPanel = new JPanel(new GridLayout());
        addButton = new JButton("Add");
        kickButton = new JButton("Kick");
        settingsButton = new JButton("Settings");

        settingsPanel.add(addButton);
        settingsPanel.add(kickButton);
        settingsPanel.add(settingsButton);
        settingsPanel.setPreferredSize(new Dimension(200, 20));
        settingsPanel.setMaximumSize(new Dimension(200, 20));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.GREEN);

        //this.add(searchBar);
        this.add(panel);
        this.add(settingsPanel);
        //this.setMaximumSize(new Dimension(200, 500));
    }

    class MemberButton extends JButton {
        public MemberButton(String memberName) {
            this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            this.setText(memberName);
        }
    }
}

class HintTextField extends JTextField implements FocusListener {

    private final String hint;
    private boolean showingHint;

    public HintTextField(final String hint) {
        super(hint);
        this.setForeground(Color.GRAY);
        this.hint = hint;
        this.showingHint = true;
        super.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setForeground(Color.BLACK);
            super.setText("");
            showingHint = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setForeground(Color.GRAY);
            super.setText(hint);
            showingHint = true;
        }
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText();
    }
}
