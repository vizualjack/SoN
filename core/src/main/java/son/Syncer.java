package son;

import java.net.Socket;

import son.network.Client;
import son.network.Server;

public class Syncer {
    int port = 1337;
    Server server;

    public Syncer() {
        server = new Server(port);
        server.onConnected = s -> connectedToClient(s);
    }

    public void sync() {
        Client client = new Client(port);
        client.onConnected = s -> connectedToServer(s);
    }

    void connectedToServer(Socket socket) {
        
    }
    
    void connectedToClient(Socket socket) {
            
    }
}
