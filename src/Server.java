import server.controller.ServerController;

public class Server {
    public static void main(String[] args) {
        // start server
        ServerController server = new ServerController();
        server.run();
    }
}
