package son;

import java.io.File;
import java.net.Socket;

import son.network.Client;
import son.network.Endpoint;
import son.network.Server;

public class Syncer {
    int port = 1337;
    Server server;
    SyncFolder syncFolder;

    private String testPw = "askdalwdijef";

    public Syncer(File folder) {
        this(new SyncFolder(folder));
    }

    public Syncer(SyncFolder syncFolder) {
        this.syncFolder = syncFolder;
        server = new Server(port);
        server.onConnected = s -> connectedToClient(s);
        server.start();
    }

    public void sync() {
        Client client = new Client(port);
        client.onConnected = s -> connectedToServer(s);
        client.connect();
    }

    void connectedToServer(Socket socket) {
        var endpoint = new Endpoint(socket);
        endpoint.sendString(testPw);
        var fileName = endpoint.readString();
        var file = new File(syncFolder.folder, fileName);
        endpoint.receiveFile(file);
    }
    
    void connectedToClient(Socket socket) {
        var endpoint = new Endpoint(socket);
        var clientPw = endpoint.readString();
        if(!testPw.equals(clientPw)) return;
        var file = syncFolder.getFiles().get(0);
        endpoint.sendString(file.getName());
        endpoint.sendFile(file);
    }
}
