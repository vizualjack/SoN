package son.sync;

import java.io.File;

import son.SyncFolder;
import son.network.Endpoint;
import son.network.packet.BasePacket;
import son.network.packet.FilePacket;
import son.network.packet.MetaFilesPacket;
import son.network.packet.PacketType;

public class Receiver {
    public Receiver(Endpoint endpoint, SyncFolder syncFolder) {
        endpoint.send(new MetaFilesPacket(syncFolder.getMetaFiles()));

        BasePacket packet;
        while((packet = endpoint.read()) != null) {
            System.out.println("packet received");
            if(packet.packetType == PacketType.END_OF_SYNC) return;
            if(packet.packetType == PacketType.FILE) {
                System.out.println("File packet received");
                var filePacket = (FilePacket)packet;
                var file = new File(syncFolder.folder, filePacket.getFilePath());
                
                if(filePacket.getSize() == 0) {
                    file.delete();
                    System.out.println("File deleted");
                }
                else {
                    // endpoint.send(new BasePacket(PacketType.SEND_FILE));
                    System.out.println("Receiving file");
                    endpoint.receiveFile(file, filePacket.getSize());
                    System.out.println("File received");
                    file.setLastModified(filePacket.getLastModified());
                }
                endpoint.send(new BasePacket(PacketType.READY));
            }
        }
    }
}
