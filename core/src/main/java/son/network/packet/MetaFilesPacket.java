package son.network.packet;

import java.util.List;

import son.MetaFile;

public class MetaFilesPacket extends BasePacket {
    List<MetaFile> metaFiles;
    
    public MetaFilesPacket(List<MetaFile> metaFiles) {
        super(PacketType.META_FILES);
        this.metaFiles = metaFiles;
    }

    public List<MetaFile> getMetaFiles() {
        return metaFiles;
    }
}   
