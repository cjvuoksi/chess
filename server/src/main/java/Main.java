import port.Port;
import server.Server;

public class Main {
    public static void main(String[] args) {
        new Server().run(Port.port);
    }
}