package son.network.packet;

public class FilePacket extends BasePacket {
    String filePath;
    long size;
    long lastModified;
    
    public FilePacket(String filePath, long size, long lastModified) {
        super(PacketType.FILE);
        this.filePath = filePath;
        this.size = size;
        this.lastModified = lastModified;
    }

    public FilePacket(String filePath) {
        super(PacketType.FILE);
        this.filePath = filePath;
        this.size = 0;
    }
    
    public String getFilePath() {
        return filePath;
    }

    public long getSize() {
        return size;
    }

    public long getLastModified() {
        return lastModified;
    }
}
