package son.sync;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import son.MetaFile;
import son.SyncFolder;
import son.compare.Comparer;
import son.network.Endpoint;
import son.network.packet.BasePacket;
import son.network.packet.FilePacket;
import son.network.packet.MetaFilesPacket;
import son.network.packet.PacketType;

public class Sender {
    private static class FileTransfer {
        enum Type{TRANSFER, DELETE}
        Type type;
        String filePath;

        FileTransfer(Type type, String filePath) {
            this.type = type;
            this.filePath = filePath;
        }

        Type getType() {
            return type;
        }
        
        String getFilePath() {
            return filePath;
        }
    }

    public Sender(Endpoint endpoint, SyncFolder syncFolder) {
        var metaFilePacket = (MetaFilesPacket) endpoint.read();        

        for (var fileTransfer : getFileTransfers(syncFolder.getMetaFiles(), metaFilePacket.getMetaFiles())) {
            if(fileTransfer.type == FileTransfer.Type.DELETE) {
                endpoint.send(new FilePacket(fileTransfer.filePath));
            }
            else {
                var syncFile = syncFolder.getSyncFile(fileTransfer.filePath);
                endpoint.send(new FilePacket(fileTransfer.filePath, syncFile.getSize(), syncFile.getLastModified()));
                endpoint.sendSyncFile(syncFile);
            }
            if(endpoint.read().packetType != PacketType.READY) {
                System.out.println("fail...");
                break;
            }
        }
        endpoint.send(new BasePacket(PacketType.END_OF_SYNC));

        // for (var file : syncFolder.getFiles()) {
        //     System.out.println("Sending filepacket");
        //     endpoint.send(new FilePacket(file.getName(), file.length()));
        //     System.out.println("wait for start send file");
        //     var packet = endpoint.read();
        //     if(packet.packetType == PacketType.SEND_FILE){
        //         System.out.println("sending file");
        //         endpoint.sendFile(file);
        //         System.out.println("file sent");
        //     }
        //     System.out.println("wait for ready packet");
        //     if(endpoint.read().packetType == PacketType.READY)
        //         System.out.println("ready packet received");
        // }
    }

    private List<FileTransfer> getFileTransfers(List<MetaFile> thisMetaFiles, List<MetaFile> otherMetaFiles) {
        var fileTransfers = new ArrayList<FileTransfer>();

        for (var thisMetaFile : thisMetaFiles) {
            var found = false;
            for (var otherMetaFile : otherMetaFiles) {
                if(thisMetaFile.path.contentEquals(otherMetaFile.path)) {
                    found = true;
                    if( !thisMetaFile.checksum.contentEquals(otherMetaFile.checksum) && 
                        thisMetaFile.lastModified > otherMetaFile.lastModified) 
                        fileTransfers.add(new FileTransfer(FileTransfer.Type.TRANSFER, thisMetaFile.path));
                    break;
                }
            }
            if(!found)
                fileTransfers.add(new FileTransfer(FileTransfer.Type.TRANSFER, thisMetaFile.path));
        }

        for (var otherMetaFile : otherMetaFiles) {
            var found = false;
            for (var thisMetaFile : thisMetaFiles) {
                if(otherMetaFile.path.contentEquals(thisMetaFile.path)) {
                    found = true;
                    break;
                }
            }
            if(!found)
                fileTransfers.add(new FileTransfer(FileTransfer.Type.DELETE, otherMetaFile.path));
        }
        
        System.out.println("fileTransfers: " + fileTransfers.size());
        return fileTransfers;
    }
}
