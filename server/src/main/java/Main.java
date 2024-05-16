import port.Port;
import server.Server;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            if (Objects.equals(args[2], "DEBUG")) {

            }
        }
        new Server().run(Port.port);
    }
}