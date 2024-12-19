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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Syncer {
    private static final Logger logger = LoggerFactory.getLogger(Syncer.class);
    
    final int SYNC_RATE_MILLIS = 30000;
    final int PORT = 1337;
    Server server;
    SyncFolder syncFolder;
    ClientHolder clientHolder;
    boolean stopping = false;

    List<byte[]> syncingClients = new ArrayList<>();

    public Syncer(SyncFolder syncFolder) {
        this.syncFolder = syncFolder;
        clientHolder = new ClientHolder(PORT);
        startServer();
        clientHolder.start();
    }

//    public Syncer(SyncFolder syncFolder, ClientHolder clientHolder) {
//        this.syncFolder = syncFolder;
//        this.clientHolder = clientHolder;
//        startServer();
//        clientHolder.start();
//    }

    private void startServer() {
        server = new Server(PORT);
        server.onConnected = s -> connectedToClient(s);
        server.start();
    }

    public void stop() {
        stopping = true;
        clientHolder.stop();
        server.stop();
    }

    public void syncLoop() {
        logger.info("Start sync loop");
        while(true) {
            try {
                clientHolder.pingToClients();
                sync();
                logger.debug(String.format("Next ping and sync in %d seconds", SYNC_RATE_MILLIS / 1000));
                Thread.sleep(SYNC_RATE_MILLIS);
            } catch (InterruptedException e) {
                if(stopping)  return;
                logger.error("Error in sync loop: ", e);
            }
        }
    }

    private void sync() {
        if(clientHolder.hasNetworkChanged()) {
            logger.debug("Network changed, restarting Syncer...");
            clientHolder.stop();
            server.stop();
            clientHolder.start();
            if(clientHolder.isActive()) {
                server.start();
                logger.debug("Syncer restarted");
            }
            else {
                logger.debug("Network is not a local one");
            }
        }
        if(!clientHolder.isActive()) return;
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

    private void connectedToServer(Socket socket) {
        logger.debug("Connected to server");
        var endpoint = new Endpoint(socket);
        var address = socket.getInetAddress().getAddress();
        if(!addToSyncingClients(address)) return;
        endpoint.send(new LastModifiedPacket(syncFolder.getLastChangeOfFolder()));
        var rolePacket = (RolePacket) endpoint.read();
        var role = rolePacket.getRole();
        logger.debug(String.format("got role: %s", role));
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
        logger.debug("Connected to client");
        var endpoint = new Endpoint(socket);
        var address = socket.getInetAddress().getAddress();
        if(!addToSyncingClients(address)) {
            logger.debug("Already syncing with this client");
            return;
        }
        var lastModifiedPacket = (LastModifiedPacket) endpoint.read();
        var lastModifiedClient = lastModifiedPacket.getLastModified() / (60 * 1000);
        var lastModified = syncFolder.getLastChangeOfFolder()  / (60 * 1000);
        logger.debug("Sync partner's last modified time: " + lastModifiedClient);
        logger.debug("My last modified: " + lastModified);
        if(lastModified != lastModifiedClient) {
            if(lastModified > lastModifiedClient) {
                logger.debug("Send receive role packet and create sender");
                endpoint.send(new RolePacket(Role.RECEIVER));
                new Sender(endpoint, syncFolder);
            }
            else {
                logger.debug("Send sender role packet and create receiver");
                endpoint.send(new RolePacket(Role.SENDER));
                new Receiver(endpoint, syncFolder);
            }
        }
        else {
            logger.debug("Nothing to do...");
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