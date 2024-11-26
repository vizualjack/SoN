package son;

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
    final int SYNC_RATE_MILLIS = 30000;
    final int PORT = 1337;
    Server server;
    SyncFolder syncFolder;
    ClientHolder clientHolder;

    List<byte[]> syncingClients = new ArrayList<>();

    public Syncer(SyncFolder syncFolder) {
        this.syncFolder = syncFolder;
        clientHolder = new ClientHolder(PORT);
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
        server = new Server(PORT);
        server.onConnected = s -> connectedToClient(s);
        server.start();
    }

    public void syncLoop() {
        System.out.println("Start sync loop");
        while(true) {
            try {
                clientHolder.pingToClients();
                sync();
                System.out.println(String.format("Next ping and sync in %d seconds", SYNC_RATE_MILLIS / 1000));
                Thread.sleep(SYNC_RATE_MILLIS);
            } catch (InterruptedException e) {
                System.out.println("Error in sync loop");
                e.printStackTrace();
            }
        }
    }

    public void sync() {
        if(clientHolder.hasNetworkChanged()) {
            System.out.println("Network changed, restarting Syncer...");
            clientHolder.stop();
            server.stop();
            clientHolder.start();
            if(clientHolder.isActive()) {
                server.start();
                System.out.println("Syncer restarted");
            }
            else {
                System.out.println("Network is not a local one");
            }
        }
        if(!clientHolder.isActive()) return;
        System.out.println("Go through clients for syncing...");
        var badClientAddresses = new ArrayList<byte[]>();
        for(var clientAddress : clientHolder.getClients()) {
            Client client = new Client(PORT);
            client.onConnected = s -> connectedToServer(s);
            client.connect(clientAddress);
            if(client.timedOut) badClientAddresses.add(clientAddress);
        }
        for(var badClientAddress : badClientAddresses) 
            clientHolder.remove(badClientAddress);
    }

    void connectedToServer(Socket socket) {
        System.out.println("Connected to server");
        var endpoint = new Endpoint(socket);
        var address = socket.getInetAddress().getAddress();
        if(!addToSyncingClients(address)) return;
        endpoint.send(new LastModifiedPacket(syncFolder.getLastChangeOfFolder()));
        var rolePacket = (RolePacket) endpoint.read();
        var role = rolePacket.getRole();
        System.out.println(String.format("got role: %s", role));
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
        if(!addToSyncingClients(address)) {
            System.out.println("Already syncing with this client");
            return;
        }
        // clientHolder.addToClients(address);
        var lastModifiedPacket = (LastModifiedPacket) endpoint.read();
        var lastModifiedClient = lastModifiedPacket.getLastModified() / (60 * 1000);
        var lastModified = syncFolder.getLastChangeOfFolder()  / (60 * 1000);
        System.out.println("lastModifiedClient: " + lastModifiedClient);
        System.out.println("lastModified: " + lastModified);
        if(lastModified != lastModifiedClient) {
            if(lastModified > lastModifiedClient) {
                System.out.println("Send receive role packet and create sender");
                endpoint.send(new RolePacket(Role.RECEIVER));
                new Sender(endpoint, syncFolder);
            }
            else {
                System.out.println("Send sender role packet and create receiver");
                endpoint.send(new RolePacket(Role.SENDER));
                new Receiver(endpoint, syncFolder);
            }
        }
        else {
            System.out.println("Nothing to do...");
            endpoint.send(new RolePacket(Role.NOTHING));
        }
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