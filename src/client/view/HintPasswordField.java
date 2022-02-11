package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class HintPasswordField extends JPasswordField implements FocusListener {

    private final String hint;
    private boolean showingHint;

    public HintPasswordField(final String hint) {
        super(hint);
        this.setForeground(Color.GRAY);
        super.setEchoChar((char) 0);
        this.hint = hint;
        this.showingHint = true;
        super.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setForeground(Color.BLACK);
            super.setEchoChar('*');
            super.setText("");
            showingHint = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setForeground(Color.GRAY);
            super.setEchoChar((char) 0);
            super.setText(hint);
            showingHint = true;
        }
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText();
    }
}