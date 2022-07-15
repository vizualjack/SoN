package son.network.packet;

public class FilePacket extends BasePacket {
    String filePath;
    long size;
    double lastModified;
    
    public FilePacket(String filePath, long size, double lastModified) {
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

    public double getLastModified() {
        return lastModified;
    }
}
