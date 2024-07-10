package client;

import communication.HttpCommunicator;
import communication.ServerFacade;

public class ServerFacadeExposer extends ServerFacade {

    public ServerFacadeExposer(int port) {
        super(port);
    }

    public HttpCommunicator getHTTPCommunicator() {
        return this.http;
    }
}
