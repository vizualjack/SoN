package son;

import java.io.File;
import java.net.Socket;

import son.network.Client;
import son.network.ClientHolder;
import son.network.Endpoint;
import son.network.Server;
import son.network.packet.BasePacket;
import son.network.packet.FilePacket;
import son.network.packet.PacketType;

public class Syncer {
    int port = 1337;
    Server server;
    SyncFolder syncFolder;
    ClientHolder clientHolder;

    private String testPw = "askdalwdijef";

    public Syncer(File folder) {
        this(new SyncFolder(folder));
    }

    public Syncer(SyncFolder syncFolder) {
        this.syncFolder = syncFolder;
        server = new Server(port);
        server.onConnected = s -> connectedToClient(s);
        clientHolder = new ClientHolder(port);
        clientHolder.start();
    }

    public void startServer() {
        server.start();
    }

    public void sync() {
        String clientAddress = clientHolder.getClient();
        if(clientAddress == null) return;
        Client client = new Client(port);
        client.onConnected = s -> connectedToServer(s);
        client.connect(clientAddress);
    }

    void connectedToServer(Socket socket) {
        System.out.println("Connected to server");
        var endpoint = new Endpoint(socket);
        endpoint.sendString(testPw);

        BasePacket packet;
        while((packet = endpoint.read()) != null) {
            System.out.println("packet received");
            if(packet.packetType == PacketType.END_OF_SYNC) return;
            if(packet.packetType == PacketType.FILE) {
                System.out.println("File packet received");
                var filePacket = (FilePacket)packet;
                var file = new File(syncFolder.folder, filePacket.getFilePath());
                endpoint.send(new BasePacket(PacketType.SEND_FILE));
                System.out.println("Receiving file");
                endpoint.receiveFile(file, filePacket.getSize());
                System.out.println("File received");
                endpoint.send(new BasePacket(PacketType.READY));
            }
        }
    }
    
    void connectedToClient(Socket socket) {
        System.out.println("Connected to client");
        var endpoint = new Endpoint(socket);
        var clientPw = endpoint.readString();
        if(!testPw.equals(clientPw)) return;

        for (var file : syncFolder.getFiles()) {
            System.out.println("Sending filepacket");
            endpoint.send(new FilePacket(file.getName(), file.length()));
            System.out.println("wait for start send file");
            var packet = endpoint.read();
            if(packet.packetType == PacketType.SEND_FILE){
                System.out.println("sending file");
                endpoint.sendFile(file);
                System.out.println("file sent");
            }
            System.out.println("wait for ready packet");
            if(endpoint.read().packetType == PacketType.READY)
                System.out.println("ready packet received");
        }
        endpoint.send(new BasePacket(PacketType.END_OF_SYNC));
    }
}
