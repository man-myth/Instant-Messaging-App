package client.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ExitOnCloseAdapter extends WindowAdapter {
    Socket socket;
    public ExitOnCloseAdapter(Socket socket) {
        this.socket = socket;
    }

    public void windowClosing(WindowEvent e) {
        try {
            socket.shutdownOutput();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ne){
            System.exit(0);
        }
        System.exit(0);
    }
}
