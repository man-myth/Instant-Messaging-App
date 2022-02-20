import server.controller.ServerController;

public class Server {
    public static void main(String[] args) {
        // Start server
        ServerController server = new ServerController();
        server.run();
    }
}
