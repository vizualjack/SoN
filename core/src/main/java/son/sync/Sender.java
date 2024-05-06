package son.sync;

import java.util.ArrayList;
import java.util.List;

import son.MetaFile;
import son.SyncFolder;
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
                System.out.println("Not ready for next file... thats weird, i will break here");
                break;
            }
        }
        endpoint.send(new BasePacket(PacketType.END_OF_SYNC));
    }

    private List<FileTransfer> getFileTransfers(List<MetaFile> thisMetaFiles, List<MetaFile> otherMetaFiles) {
        var fileTransfers = new ArrayList<FileTransfer>();

        for (var thisMetaFile : thisMetaFiles) {
            var found = false;
            for (var otherMetaFile : otherMetaFiles) {
                if(thisMetaFile.path.contentEquals(otherMetaFile.path)) {
                    found = true;
                    if(!thisMetaFile.checksum.contentEquals(otherMetaFile.checksum) && 
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
        return fileTransfers;
    }
}
