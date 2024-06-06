package server;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

import static port.Port.port;

@ClientEndpoint
public class TestClient extends Endpoint {

    Session session;

    public static void main(String[] args) throws InterruptedException {
        try {
            new TestClient().run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TestClient() {
        URI uri = URI.create("ws://localhost:" + port + "/echo");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            this.session = container.connectToServer(this, uri);
        } catch (DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    Scanner scanner = new Scanner(System.in);

    private void run() throws IOException {
        String input = "init";
        while (!"quit".equals(input)) {
            input = scanner.nextLine();
            send(input);
        }
        scanner.close();
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                if ("Error".equals(message)) {
                    throw new RuntimeException("Error");
                }
                System.out.println(message);
            }
        });
    }

    private void send(String message) {
        if (this.session.isOpen()) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        System.out.println(closeReason.getReasonPhrase());
        throw new RuntimeException();
    }
}
