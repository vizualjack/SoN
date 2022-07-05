package son;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import son.network.Client;
import son.network.ClientHolder;
import son.network.Endpoint;
import son.network.InetAddressHelper;
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

    List<byte[]> syncingClients = new ArrayList<>();

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
        var address = socket.getInetAddress().getAddress();
        if(!addToSyncingClients(address)) return;

        endpoint.send(new LastModifiedPacket(syncFolder.getLastChangeOfFolder()));

        var rolePacket = (RolePacket) endpoint.read();
        var role = rolePacket.getRole();

        switch(role) {
            case RECEIVER:
                new Receiver(endpoint, syncFolder);
                break;
            case SENDER:
                new Sender(endpoint, syncFolder);
                break;
            default:
        }

        removeFromSyncingClients(address);
    }

    void connectedToClient(Socket socket) {
        System.out.println("Connected to client");
        var endpoint = new Endpoint(socket);
        var address = socket.getInetAddress().getAddress();
        if(!addToSyncingClients(address)) return;

        clientHolder.addToClients(address);
        var lastModifiedPacket = (LastModifiedPacket) endpoint.read();
        var lastModifiedClient = lastModifiedPacket.getLastModified();
        var lastModified = syncFolder.getLastChangeOfFolder();

        if(lastModified != lastModifiedClient) {
            if(lastModified > lastModifiedClient) {
                endpoint.send(new RolePacket(Role.RECEIVER));
                new Sender(endpoint, syncFolder);
            }
            else {
                endpoint.send(new RolePacket(Role.SENDER));
                new Receiver(endpoint, syncFolder);
            }
        }
        else
            endpoint.send(new RolePacket(Role.NOTHING));

        removeFromSyncingClients(address);
    }

    private boolean addToSyncingClients(byte[] addrToAdd) {
        for(var addr : syncingClients) {
            if(InetAddressHelper.compareAddresses(addr, addrToAdd)) {
                return false;
            }
        }
        syncingClients.add(addrToAdd);
        return true;
    }

    private void removeFromSyncingClients(byte[] addrToDelete) {
        byte[] deleteAddr = null;
        for(var addr : syncingClients) {
            if(InetAddressHelper.compareAddresses(addr, addrToDelete)) {
                deleteAddr = addr;
                break;
            }
        }
        if(deleteAddr != null) syncingClients.remove(deleteAddr);
    }
}