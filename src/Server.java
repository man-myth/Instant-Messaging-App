import server.controller.ServerController;
import server.model.UserModel;
import server.model.Utility;

import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        // Start server
        ServerController server = new ServerController();
        server.run();
    }
}
