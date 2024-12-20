package son.sync;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import son.SyncFolder;
import son.network.Endpoint;
import son.network.packet.BasePacket;
import son.network.packet.FilePacket;
import son.network.packet.MetaFilesPacket;
import son.network.packet.PacketType;

public class Receiver {
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    public Receiver(Endpoint endpoint, SyncFolder syncFolder) {
        endpoint.send(new MetaFilesPacket(syncFolder.getMetaFiles()));
        BasePacket packet;
        while((packet = endpoint.read()) != null) {
            if(packet.packetType == PacketType.END_OF_SYNC) return;
            if(packet.packetType == PacketType.FILE) {
                var filePacket = (FilePacket)packet;
                logger.debug("FilePacket Path: {}, Size: {}", filePacket.getFilePath(), filePacket.getSize());
                var syncFile = syncFolder.getSyncFile(filePacket.getFilePath());
                if(filePacket.getSize() == 0) {
                    if(syncFile != null) {
                        syncFile.delete();
                        logger.info("File deleted: {}", syncFile.getPath());
                    }
                }
                else {
                    if(syncFile == null) {
                        syncFile = syncFolder.createSyncFile(filePacket.getFilePath());
                    }
                    endpoint.receiveSyncFile(syncFile, filePacket.getSize());
                    logger.info("File received: {}", syncFile.getPath());
                }
                endpoint.send(new BasePacket(PacketType.READY));
            }
        }
    }
}
