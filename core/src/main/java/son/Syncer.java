package son;

import java.io.File;
import java.net.Socket;

import son.network.Client;
import son.network.ClientHolder;
import son.network.Endpoint;
import son.network.Server;
import son.network.packet.LastModifiedPacket;
import son.network.packet.RolePacket;
import son.network.packet.RolePacket.Role;
import son.sync.Receiver;
import son.sync.Sender;

public class Syncer {
    int port = 1337;
    Server server;
    SyncFolder syncFolder;
    ClientHolder clientHolder;

    private String testPw = "askdalwdijef";

    public Syncer(File folder) {
        this(new SyncFolder(folder));
    }

    public Syncer(File folder, ClientHolder clientHolder) {
        this(new SyncFolder(folder), clientHolder);
    }

    public Syncer(SyncFolder syncFolder) {
        this.syncFolder = syncFolder;
        clientHolder = new ClientHolder(port);
        startServer();
        clientHolder.start();
    }

    public Syncer(SyncFolder syncFolder, ClientHolder clientHolder) {
        this.syncFolder = syncFolder;
        this.clientHolder = clientHolder;
        startServer();
        clientHolder.start();
    }

    private void startServer() {
        server = new Server(port);
        server.onConnected = s -> connectedToClient(s);
        server.start();
    }

    public void sync() {
        for(var clientAddress : clientHolder.getClients()) {
            Client client = new Client(port);
            client.onConnected = s -> connectedToServer(s);
            client.connect(clientAddress);
            if(client.timedOut) clientHolder.remove(clientAddress);
        }
    }

    void connectedToServer(Socket socket) {
        System.out.println("Connected to server");
        var endpoint = new Endpoint(socket);
        endpoint.send(new LastModifiedPacket(syncFolder.getLastChangeOfFolder()));

        var rolePacket = (RolePacket) endpoint.read();
        var role = rolePacket.getRole();

        if(role == Role.RECEIVER) {
            new Receiver(endpoint, syncFolder);
        }
        else {
            new Sender(endpoint, syncFolder);
        }
    }
    
    void connectedToClient(Socket socket) {
        System.out.println("Connected to client");
        var endpoint = new Endpoint(socket);
        clientHolder.addToClients(socket.getInetAddress().getHostAddress());
        var lastModifiedPacket = (LastModifiedPacket) endpoint.read();
        var lastModifiedClient = lastModifiedPacket.getLastModified();
        var lastModified = syncFolder.getLastChangeOfFolder();

        if(lastModified > lastModifiedClient) {
            endpoint.send(new RolePacket(Role.RECEIVER));
            new Sender(endpoint, syncFolder);
        }
        else {
            endpoint.send(new RolePacket(Role.SENDER));
            new Receiver(endpoint, syncFolder);
        }
    }
}